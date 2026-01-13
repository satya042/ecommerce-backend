package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.event.OrderPlacedEvent;
import com.ecommerce.orderservice.request.OrderUpdateRequest;
import com.ecommerce.orderservice.response.OrderLineItemResponse;
import com.ecommerce.orderservice.response.OrderResponse;
import jakarta.transaction.Transactional;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderItem;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.request.OrderLineItemsDto;
import com.ecommerce.orderservice.request.OrderRequest;
import com.ecommerce.orderservice.response.InventoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private WebClient.Builder webClientBuiler;

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	public String placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		order.setStatus(com.ecommerce.orderservice.entity.enums.OrderStatus.CREATED);
		order.setCreatedAt(LocalDateTime.now());
		order.setUpdatedAt(LocalDateTime.now());

		List<OrderItem> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToOrderItem)
				.collect(Collectors.toList());

		order.setItems(orderLineItems);
		orderLineItems.forEach(item -> item.setOrder(order));

		// Calculate total amount
		BigDecimal totalAmount = orderLineItems.stream()
				.map(item -> item.getTotalPrice())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		order.setTotalAmount(totalAmount);
		order.setPaymentStatus("PENDING");

		List<String> skuCodes = orderLineItems.stream().map(OrderItem::getSkuCode).toList();

		// Call Inventory service , and place order if product is in stock
		InventoryResponse[] inventoryResponseArray = webClientBuiler.build().get()
				.uri("http://inventory-service/api/inventory",uriBuilder -> uriBuilder.queryParam("skucode", skuCodes).build())
				.retrieve().bodyToMono(InventoryResponse[].class).block();

		boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

		if (allProductsInStock) {
			orderRepository.save(order);
			kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
			return "Order Placed successfully";
		} else {
			throw new IllegalArgumentException("Product is not in stock , please try again later");
		}
	}

	public OrderResponse getOrderById(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
		return mapToOrderResponse(order);
	}

	public OrderResponse getOrderByOrderNumber(String orderNumber) {
		Order order = orderRepository.findByOrderNumber(orderNumber)
				.orElseThrow(() -> new NoSuchElementException("Order not found with orderNumber: " + orderNumber));
		return mapToOrderResponse(order);
	}

	public List<OrderResponse> getAllOrders() {
		List<Order> orders = orderRepository.findAll();
		return orders.stream().map(this::mapToOrderResponse).collect(Collectors.toList());
	}

	public List<OrderResponse> getOrdersByUserId(Long userId) {
		List<Order> orders = orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
		return orders.stream().map(this::mapToOrderResponse).collect(Collectors.toList());
	}

	public OrderResponse updateOrder(Long orderId, OrderUpdateRequest updateRequest) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));

		if (updateRequest.getStatus() != null) {
			order.setStatus(updateRequest.getStatus());
		}
		if (updateRequest.getPaymentMethod() != null) {
			order.setPaymentMethod(updateRequest.getPaymentMethod());
		}
		if (updateRequest.getPaymentStatus() != null) {
			order.setPaymentStatus(updateRequest.getPaymentStatus());
		}
		if (updateRequest.getTotalAmount() != null) {
			order.setTotalAmount(updateRequest.getTotalAmount());
		}

		order.setUpdatedAt(LocalDateTime.now());
		Order updatedOrder = orderRepository.save(order);
		return mapToOrderResponse(updatedOrder);
	}

	public void deleteOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
		orderRepository.delete(order);
	}

	private OrderItem mapToOrderItem(OrderLineItemsDto orderLineItemsDto) {
		OrderItem orderItem = new OrderItem();
		orderItem.setProductId(orderLineItemsDto.getId());
		orderItem.setPricePerUnit(orderLineItemsDto.getPrice());
		orderItem.setQuantity(orderLineItemsDto.getQuantity());
		orderItem.setSkuCode(orderLineItemsDto.getSkuCode());
		// Calculate total price
		BigDecimal totalPrice = orderLineItemsDto.getPrice().multiply(new BigDecimal(orderLineItemsDto.getQuantity()));
		orderItem.setTotalPrice(totalPrice);
		return orderItem;
	}

	private OrderResponse mapToOrderResponse(Order order) {
		OrderResponse orderResponse = new OrderResponse();
		orderResponse.setId(order.getId());
		orderResponse.setOrderNumber(order.getOrderNumber());
		orderResponse.setUserId(order.getUserId());
		orderResponse.setStatus(order.getStatus());
		orderResponse.setTotalAmount(order.getTotalAmount());
		orderResponse.setPaymentMethod(order.getPaymentMethod());
		orderResponse.setPaymentStatus(order.getPaymentStatus());
		orderResponse.setCreatedAt(order.getCreatedAt());
		orderResponse.setUpdatedAt(order.getUpdatedAt());

		List<OrderLineItemResponse> itemResponses = order.getItems().stream()
				.map(item -> new OrderLineItemResponse(
						item.getId(),
						item.getProductId(),
						item.getProductName(),
						item.getPricePerUnit(),
						item.getQuantity(),
						item.getTotalPrice()
				))
				.collect(Collectors.toList());

		orderResponse.setItems(itemResponses);
		return orderResponse;
	}

	private String mapToDto(OrderLineItemsDto orderLineItemsDto) {
		return orderLineItemsDto.getSkuCode();
	}

}

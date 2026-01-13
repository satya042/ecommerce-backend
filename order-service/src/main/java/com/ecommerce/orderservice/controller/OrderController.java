package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.request.OrderRequest;
import com.ecommerce.orderservice.request.OrderUpdateRequest;
import com.ecommerce.orderservice.response.OrderResponse;
import com.ecommerce.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	/**
	 * Create a new order
	 * POST /api/order
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@CircuitBreaker(name="inventory",fallbackMethod = "fallbackMethod")
	@TimeLimiter(name ="inventory")
	@Retry(name="inventory")
	public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) {
		return CompletableFuture.supplyAsync(()-> orderService.placeOrder(orderRequest));
	}

	/**
	 * Get all orders
	 * GET /api/order
	 */
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
		public ResponseEntity<List<OrderResponse>> getAllOrders() {
			List<OrderResponse> orders = orderService.getAllOrders();
			return ResponseEntity.ok(orders);
		}

		/**
	 * Get order by ID
	 * GET /api/order/{id}
	 */
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
		OrderResponse order = orderService.getOrderById(id);
		return ResponseEntity.ok(order);
	}

	/**
	 * Get order by order number
	 * GET /api/order/number/{orderNumber}
	 */
	@GetMapping("/number/{orderNumber}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
		OrderResponse order = orderService.getOrderByOrderNumber(orderNumber);
		return ResponseEntity.ok(order);
	}

	/**
	 * Get orders by user ID
	 * GET /api/order/user/{userId}
	 */
	@GetMapping("/user/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
		List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
		return ResponseEntity.ok(orders);
	}

	/**
	 * Update an order
	 * PUT /api/order/{id}
	 */
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<OrderResponse> updateOrder(
			@PathVariable Long id,
			@RequestBody OrderUpdateRequest updateRequest) {
		OrderResponse updatedOrder = orderService.updateOrder(id, updateRequest);
		return ResponseEntity.ok(updatedOrder);
	}

	/**
	 * Delete an order
	 * DELETE /api/order/{id}
	 */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
		orderService.deleteOrder(id);
		return ResponseEntity.noContent().build();
	}

	public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException){
		return  CompletableFuture.supplyAsync(()-> "Oops! Something went wrong, please order after some time!");
	}
}

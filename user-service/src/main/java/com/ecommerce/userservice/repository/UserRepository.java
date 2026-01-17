package com.ecommerce.userservice.repository;

import com.ecommerce.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByPhone(String phone);
    
//    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
//    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
//
//    @Query("SELECT u FROM User u WHERE u.isEnabled = true AND (u.username = :usernameOrEmail OR u.email = :usernameOrEmail)")
//    Optional<User> findActiveUserByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
}

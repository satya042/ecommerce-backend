package com.ecommerce.userservice.repository;

import com.ecommerce.userservice.entity.RefreshTokenStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenStorageRepository extends JpaRepository<RefreshTokenStorage, Long> {
    
    boolean existsByRefreshToken(String refreshToken);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshTokenStorage r SET r.isRevoked = true WHERE r.username = :username AND r.isRevoked = false")
    void revokeAllTokensByUsername(@Param("username") String username);
    
    @Query("SELECT r FROM RefreshTokenStorage r WHERE r.refreshToken = :refreshToken AND r.isRevoked = false")
    Optional<RefreshTokenStorage> findActiveRefreshToken(@Param("refreshToken") String refreshToken);
}

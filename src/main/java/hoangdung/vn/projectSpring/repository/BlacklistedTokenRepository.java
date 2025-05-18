package hoangdung.vn.projectSpring.repository;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import hoangdung.vn.projectSpring.entity.BlacklistedToken;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByToken(String token);
    int deleteByExpiryDateBefore(LocalDateTime expiryDate);

}
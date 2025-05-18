package hoangdung.vn.projectSpring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hoangdung.vn.projectSpring.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    boolean existsByRefreshToken(String refreshToken);
    Optional<RefreshToken> findByRefreshToken(String token);
    Optional<RefreshToken> findByUserId(Long userId);
    
}

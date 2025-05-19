package hoangdung.vn.projectSpring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hoangdung.vn.projectSpring.entity.PasswordReset;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByToken(String token);
    PasswordReset findByUserId(Long userId);
}

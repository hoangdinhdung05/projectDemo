package hoangdung.vn.projectSpring.helper;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import hoangdung.vn.projectSpring.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;

@Service
public class RefreshTokenClean {
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void cleanRefreshToken() {
        LocalDateTime expiryDate = LocalDateTime.now();
        refreshTokenRepository.deleteByExpiryDateBefore(expiryDate);
    }
}



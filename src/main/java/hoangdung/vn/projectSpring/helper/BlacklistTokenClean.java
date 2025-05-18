package hoangdung.vn.projectSpring.helper;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import hoangdung.vn.projectSpring.repository.BlacklistedTokenRepository;
import jakarta.transaction.Transactional;

@Service

public class BlacklistTokenClean {
    
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Chạy hàng ngày lúc 00:00
    // @Scheduled(fixedRate = 86400000) // Chạy mỗi 24 giờ
    public void cleanBlacklistedTokens() {
        LocalDateTime expiryDate = LocalDateTime.now();
        blacklistedTokenRepository.deleteByExpiryDateBefore(expiryDate);
    }

}
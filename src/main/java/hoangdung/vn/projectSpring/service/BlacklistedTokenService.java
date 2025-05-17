package hoangdung.vn.projectSpring.service;

import java.time.ZoneId;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import hoangdung.vn.projectSpring.dto.request.BlacklistedTokenRequest;
import hoangdung.vn.projectSpring.dto.response.MessageResponse;
import hoangdung.vn.projectSpring.entity.BlacklistedToken;
import hoangdung.vn.projectSpring.repository.BlacklistedTokenRepository;
import io.jsonwebtoken.Claims;

@Service
public class BlacklistedTokenService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private final JwtService jwtService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public BlacklistedTokenService(JwtService jwtService, BlacklistedTokenRepository blacklistedTokenRepository) {
        this.jwtService = jwtService;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    public Object create(BlacklistedTokenRequest request) {
        try {

            //check token exists in blacklist
            if(this.blacklistedTokenRepository.existsByToken(request.getToken())) {
                logger.info("Token already exists in blacklist");
                return new MessageResponse("Token already exists in blacklist");
            }

            Claims claims = this.jwtService.getAllClaimsFromToken(request.getToken());
            Long userId = Long.parseLong(claims.getSubject());
            Date epiryDate = claims.getExpiration();

            BlacklistedToken blacklistedToken = new BlacklistedToken();
            blacklistedToken.setToken(request.getToken());
            blacklistedToken.setUserId(userId);
            blacklistedToken.setExpiryDate(epiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            blacklistedTokenRepository.save(blacklistedToken);

            logger.info("Blacklisted token created successfully");
            return new MessageResponse("Blacklisted token created successfully"); 

        } catch (Exception e) {
            logger.error("Error creating blacklisted token: {}", e.getMessage());
            return new MessageResponse("Error creating blacklisted token");
        }
    }



    
}

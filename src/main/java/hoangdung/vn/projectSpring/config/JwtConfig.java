package hoangdung.vn.projectSpring.config;

import java.security.Key;
import java.util.Base64;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expirationRefreshToken}")
    private long refreshTokenExpirationTime;

    public String getSecretKey() {
        return secretKey;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public String getIssuer() {
        return issuer;
    }

    public long getRefreshExpirationTime() {
        return refreshTokenExpirationTime;
    }

    //tạo signature key
    /**
     * 1. Dùng để generate JWT token
     * 2. Dùng để validate JWT token
     * 3. Dùng để giải mã JWT token
     */

    @Bean
    public Key jwtSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

}

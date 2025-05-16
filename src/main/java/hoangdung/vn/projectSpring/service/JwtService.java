package hoangdung.vn.projectSpring.service;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import hoangdung.vn.projectSpring.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {
    
    private final JwtConfig jwtConfig;
    private final Key key;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = jwtConfig.jwtSigningKey();
    }

    //genarate JWT token
    //dùng id  và email của user để tạo JWT token
    public String generateToken(Long userId, String email) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationTime());

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}

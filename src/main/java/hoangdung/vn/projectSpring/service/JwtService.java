package hoangdung.vn.projectSpring.service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.springframework.stereotype.Service;
import hoangdung.vn.projectSpring.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    private final JwtConfig jwtConfig;
    private final Key key;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(jwtConfig.getSecretKey().getBytes()));
    }

    //genarate JWT token
    //dùng id  và email của user để tạo JWT token
    public String generateToken(long userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationTime());

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(now)
                .setIssuer(jwtConfig.getIssuer())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //get userId from JWT token
    public String getUserIdFromJwt(String token) {
        System.out.println("get key: " + key);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        System.out.println("get claims: " + claims);
        // System.out.println("get subject: " + claims.getSubject());
        return claims.getSubject();
    }

    public Claims getPayloadFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    private <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //get email from JWT token
    public String getEmailFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("email", String.class);
    }


     /*
     * 1. Check token có đúng định dạng không
     * 2. Check token có hết hạn không
     * 3. Check secret có đúng không
     * 4. Check userId có tồn tại không (có matches với userId trong DB không)
     * 5. Check token có trong blacklist không
     */

    //check định dạng
    public boolean isTokenFormatValid(String token) {
        try {
            
            String[] parts = token.split("\\.");
            if(parts.length != 3) {
                return false;
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    //check secret
    public boolean isSignatureValid(String token) {
        try {

            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    //get secret
    public Key getSecretKey() {
        byte[] keyBytes = this.jwtConfig.getSecretKey().getBytes();
        return Keys.hmacShaKeyFor(Base64.getEncoder().encode(keyBytes));
    }

    //check token có hết hạn không
    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = getClaimFromToken(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    public boolean isIssuerToken(String token) {
        String tokenIssuer = getClaimFromToken(token, Claims::getIssuer);
        return this.jwtConfig.getIssuer().equals(tokenIssuer);
    }
}

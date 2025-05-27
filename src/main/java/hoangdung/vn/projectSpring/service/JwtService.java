package hoangdung.vn.projectSpring.service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import hoangdung.vn.projectSpring.config.JwtConfig;
import hoangdung.vn.projectSpring.entity.Permission;
import hoangdung.vn.projectSpring.entity.RefreshToken;
import hoangdung.vn.projectSpring.entity.Role;
import hoangdung.vn.projectSpring.repository.BlacklistedTokenRepository;
import hoangdung.vn.projectSpring.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    private final JwtConfig jwtConfig;
    private final Key key;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtService(JwtConfig jwtConfig, BlacklistedTokenRepository blacklistedTokenRepository, RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(jwtConfig.getSecretKey().getBytes()));
    }

    //genarate JWT token
    //dùng id  và email của user để tạo JWT token
    public String generateToken(long userId, String email, Set<Role> roles, Long expirationTime) {
        Date now = new Date();
        if (expirationTime == null) {
            expirationTime = jwtConfig.getExpirationTime();
        }
        Date expiryDate = new Date(now.getTime() + expirationTime);

        // Lấy danh sách tên role
        List<String> roleNames = roles.stream()
                .map(Role::getName)
                .toList();

        // Lấy danh sách tên permission từ tất cả roles
        Set<String> permissions = roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("roles", roleNames)
                .claim("permissions", permissions)
                .setIssuedAt(now)
                .setIssuer(jwtConfig.getIssuer())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateRefreshToken(long userId, String email) {
        System.out.println("generate refresh token");
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshExpirationTime());

        String refreshToken = UUID.randomUUID().toString();
        LocalDateTime localDate  = expiryDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();

        Optional<RefreshToken> refreshTokenOptional = this.refreshTokenRepository.findByUserId(userId);
        if(refreshTokenOptional.isPresent()) {
            RefreshToken refreshTokenEntity = refreshTokenOptional.get();
            refreshTokenEntity.setRefreshToken(refreshToken);
            refreshTokenEntity.setExpiryDate(localDate);
            this.refreshTokenRepository.save(refreshTokenEntity);
        } else {
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setUserId(userId);
            refreshTokenEntity.setRefreshToken(refreshToken);
            refreshTokenEntity.setExpiryDate(localDate);
            this.refreshTokenRepository.save(refreshTokenEntity);
        }

        System.out.println("refresh token: " + refreshToken);

        return refreshToken;

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

    public <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
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

    //check token có trong blacklist không
    public boolean isBlacklistedToken(String token) {
        return this.blacklistedTokenRepository.existsByToken(token);
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            RefreshToken refreshToken = this.refreshTokenRepository.findByRefreshToken(token).orElseThrow(() -> 
                new RuntimeException("Refresh token not found"));
            LocalDateTime expirationLocalDateTime  = refreshToken.getExpiryDate();
            Date expirationDate = Date.from(expirationLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
            return expirationDate.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}

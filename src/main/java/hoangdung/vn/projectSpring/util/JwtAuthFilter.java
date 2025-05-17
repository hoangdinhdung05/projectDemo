package hoangdung.vn.projectSpring.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import hoangdung.vn.projectSpring.service.JwtService;
import hoangdung.vn.projectSpring.service.impl.CustomUserDetailsService;
import io.jsonwebtoken.Claims;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
/**
 * JwtAuthFilter is a filter that checks the JWT token in the request header.
 * If the token is valid, it sets the authentication in the security context.
 * If the token is invalid, it sends an error response.
 */
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    protected boolean shouldNotFilter(
        @NonNull HttpServletRequest request){
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/login") || 
        path.startsWith("/api/v1/auth/refresh") || 
        path.startsWith("/swagger-ui") || 
        path.startsWith("/swagger-ui/**") || 
        path.startsWith("/v3/api-docs" ) ||
        path.startsWith("/swagger-resources/**" ) ||
        path.startsWith("/webjars/**") || 
        path.startsWith("/api-docs/swagger-config") || 
        path.startsWith("/api-docs")
        ;
    }

    @Override
    public void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
    
        try {

            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userId;

            //Check header token
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                // logger.info("Token không hợp lệ");
                sendErrorResponse(
                    response, 
                    request, 
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Xác thực không thành công", 
                    "Không tìm thấy token"
                );
                return;
            }

            //lấy token từ header
            jwt = authHeader.substring(7);
            //Check token 
            if(!this.jwtService.isTokenFormatValid(jwt)) {
                sendErrorResponse(
                    response, 
                    request,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Xác thực không thành công",
                    "Token không đúng định dạng"
                );
                return;
            }
            
            if(this.jwtService.isTokenExpired(jwt)) {
                sendErrorResponse(
                    response,
                    request,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Xác thực không thành công",
                    "Token đã hết hạn"
                );
                return;
            }

            if(!jwtService.isSignatureValid(jwt)){
                sendErrorResponse(response, 
                    request, 
                    HttpServletResponse.SC_UNAUTHORIZED, 
                    "Xác thực không thành công", 
                    "Chữ ký không hợp lệ."
                );
                return;
            }

            if(!jwtService.isIssuerToken(jwt)){
                sendErrorResponse(response, 
                    request, 
                    HttpServletResponse.SC_UNAUTHORIZED, 
                    "Xác thực không thành công", 
                    "Nguồn Token không hợp lệ."
                );
                return;
            }

            //check blacklist
            if(this.jwtService.isBlacklistedToken(jwt)) {
                sendErrorResponse(response, 
                    request, 
                    HttpServletResponse.SC_UNAUTHORIZED, 
                    "Xác thực không thành công", 
                    "Token đã bị thu hồi."
                );
                return;
            }
            
            //Lấy userId từ token
            userId = this.jwtService.getUserIdFromJwt(jwt);
            Claims payload = this.jwtService.getPayloadFromToken(jwt);

            if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(payload.get("email", String.class));
                //get Username from token
                String emailFromToken = this.jwtService.getEmailFromJwt(jwt);
                //Check username 
                if(!emailFromToken.equals(userDetails.getUsername())) {
                    sendErrorResponse(
                        response, 
                        request, 
                        HttpServletResponse.SC_UNAUTHORIZED, 
                        "Xác thực không thành công", 
                        "Tài khoản không hợp lệ."
                    );
                    return;
                }
                //username hợp lệ
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities()
                );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                        .buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Xác thực thành công");

            }

            filterChain.doFilter(request, response); 

        } catch (ServletException | IOException e) {
            sendErrorResponse(response, 
                request, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Network Error!", 
                e.getMessage()
            );
        }
        
    }

    private void sendErrorResponse(
        @NotNull HttpServletResponse response,
        @NotNull HttpServletRequest request,
        int statusCode,
        String error,
        String message) throws IOException {

        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>(); 

        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("status", statusCode);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", request.getRequestURI());

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }

}

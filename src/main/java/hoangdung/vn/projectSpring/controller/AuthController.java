package hoangdung.vn.projectSpring.controller;

import hoangdung.vn.projectSpring.dto.UserDTO;
import hoangdung.vn.projectSpring.dto.request.BlacklistedTokenRequest;
import hoangdung.vn.projectSpring.dto.request.LoginRequest;
import hoangdung.vn.projectSpring.dto.request.RefreshTokenRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.LoginResponse;
import hoangdung.vn.projectSpring.dto.response.MessageResponse;
import hoangdung.vn.projectSpring.dto.response.RefreshTokenResponse;
import hoangdung.vn.projectSpring.entity.RefreshToken;
import hoangdung.vn.projectSpring.entity.Role;
import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.repository.RefreshTokenRepository;
import hoangdung.vn.projectSpring.repository.UserRepository;
import hoangdung.vn.projectSpring.service.BlacklistedTokenService;
import hoangdung.vn.projectSpring.service.JwtService;
import hoangdung.vn.projectSpring.service.interfaces.UserInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserInterface userInterface;
    private final BlacklistedTokenService blacklistedTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        
        Object result = this.userInterface.authenticate(request);

        if(result instanceof LoginResponse loginResponse) {
            ApiResponse<LoginResponse> response = ApiResponse.ok(loginResponse, "Success");
            return ResponseEntity.ok(response);
        } 

        if(result instanceof ApiResponse errorResponse) {
            return ResponseEntity.unprocessableEntity().body(errorResponse);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Network error");
    }

    @PostMapping("/blacklist")
    public ResponseEntity<?> blackListToken(@RequestBody @Valid BlacklistedTokenRequest request) {
        try {

            Object result = this.blacklistedTokenService.create(request);
            return ResponseEntity.ok(result);
    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Network error");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            BlacklistedTokenRequest request = new BlacklistedTokenRequest();
            request.setToken(jwt);

            this.blacklistedTokenService.create(request);
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .message("Đăng xuất thành công")
                    .status(HttpStatus.OK)
                    .build();
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                    .success(false)
                    .message("Token không hợp lệ")
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. Kiểm tra refresh token hợp lệ
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("RefreshToken không hợp lệ"));
        }

        // 2. Tìm refresh token trong DB
        Optional<RefreshToken> dbRefreshTokenOptional = this.refreshTokenRepository.findByRefreshToken(refreshToken);

        if (dbRefreshTokenOptional.isPresent()) {
            RefreshToken dbRefreshToken = dbRefreshTokenOptional.get();
            Long userId = dbRefreshToken.getUserId();

            // 3. Lấy user theo userId
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User không tồn tại"));
            }
            User user = userOptional.get();

            // 4. Lấy roles của user
            Set<Role> roles = user.getRoles();

            // 5. Tạo token mới có roles (và có thể permissions nếu bạn muốn)
            String newToken = jwtService.generateToken(userId, user.getEmail(), roles, null);

            // 6. Tạo refresh token mới
            String newRefreshToken = jwtService.generateRefreshToken(userId, user.getEmail());

            // 7. Tạo response trả về
            RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(newToken, newRefreshToken);
            return ResponseEntity.ok(refreshTokenResponse);
        }

        // 8. Nếu không tìm thấy refresh token trong DB
        return ResponseEntity.internalServerError()
            .body(new MessageResponse("Network Error!"));
    }


    @GetMapping("/me")
    public ResponseEntity<?> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                // .phone(user.getPhone())
                .build();
        //convert sang success response
        // SuccessResponse<UserDTO> successResponse = new SuccessResponse<UserDTO>("Success", userDTO);
        ApiResponse<UserDTO> response = ApiResponse.ok(userDTO, "Success");
        return ResponseEntity.ok(response);  
    }

}

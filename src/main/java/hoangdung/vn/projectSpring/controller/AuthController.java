package hoangdung.vn.projectSpring.controller;

import hoangdung.vn.projectSpring.dto.UserDTO;
import hoangdung.vn.projectSpring.dto.request.BlacklistedTokenRequest;
import hoangdung.vn.projectSpring.dto.request.LoginRequest;
import hoangdung.vn.projectSpring.dto.request.RefreshTokenRequest;
import hoangdung.vn.projectSpring.dto.response.ErrorResponse;
import hoangdung.vn.projectSpring.dto.response.LoginResponse;
import hoangdung.vn.projectSpring.dto.response.MessageResponse;
import hoangdung.vn.projectSpring.dto.response.RefreshTokenResponse;
import hoangdung.vn.projectSpring.dto.response.SuccessResponse;
import hoangdung.vn.projectSpring.entity.RefreshToken;
import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.repository.RefreshTokenRepository;
import hoangdung.vn.projectSpring.repository.UserRepository;
import hoangdung.vn.projectSpring.service.BlacklistedTokenService;
import hoangdung.vn.projectSpring.service.JwtService;
import hoangdung.vn.projectSpring.service.interfaces.UserInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            return ResponseEntity.ok(loginResponse);
        } 

        if(result instanceof ErrorResponse errorResponse) {
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

            Object result = this.blacklistedTokenService.create(request);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Network error");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if(!jwtService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("RefreshToken không hợp lệ"));
        }

        Optional<RefreshToken> dbRefreshTokenOptional = this.refreshTokenRepository.findByRefreshToken(refreshToken);


        if(dbRefreshTokenOptional.isPresent()) {
            RefreshToken dBRefreshToken = dbRefreshTokenOptional.get();
            Long userId = dBRefreshToken.getUserId();
            String email = dBRefreshToken.getUser().getEmail();
    
            String newToken = jwtService.generateToken(userId, email);
            String newRefreshToken = jwtService.generateRefreshToken(userId, email);
            RefreshTokenResponse refreshTokenResource = new RefreshTokenResponse(newToken, newRefreshToken);
            
            return ResponseEntity.ok(refreshTokenResource);
        }
        return ResponseEntity.internalServerError().body(new MessageResponse("Netword Error !"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        String email = "hoangdinhdung0205@gmail.com";
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> 
                new RuntimeException("User not found"));
        UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), user.getName());
        //convert sang success response
        SuccessResponse<UserDTO> successResponse = new SuccessResponse<UserDTO>("Success", userDTO);
        return ResponseEntity.ok(successResponse);  
    }

}

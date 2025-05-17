package hoangdung.vn.projectSpring.controller;

import hoangdung.vn.projectSpring.dto.request.BlacklistedTokenRequest;
import hoangdung.vn.projectSpring.dto.request.LoginRequest;
import hoangdung.vn.projectSpring.dto.response.ErrorResponse;
import hoangdung.vn.projectSpring.dto.response.LoginResponse;
import hoangdung.vn.projectSpring.service.BlacklistedTokenService;
import hoangdung.vn.projectSpring.service.interfaces.UserInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final UserInterface userInterface;
    private final BlacklistedTokenService blacklistedTokenService;

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

}

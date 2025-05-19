package hoangdung.vn.projectSpring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.ResetPasswordResponse;
import hoangdung.vn.projectSpring.service.PasswordResetService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        System.out.println("email: " + email);
        passwordResetService.createPasswordResetToken(email);
        ApiResponse<?> response = ApiResponse.ok(null, "Vui lòng check lại email");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                        @RequestParam String newPassword) {
        ResetPasswordResponse resetResponse = passwordResetService.resetPassword(token, newPassword);
        System.out.println("resetResponse: " + resetResponse);
        ApiResponse<ResetPasswordResponse> response = ApiResponse.ok(resetResponse, "Success");
        return ResponseEntity.ok(response);
    }

}
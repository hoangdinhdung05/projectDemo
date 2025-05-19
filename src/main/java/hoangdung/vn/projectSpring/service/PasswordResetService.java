package hoangdung.vn.projectSpring.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hoangdung.vn.projectSpring.dto.response.ResetPasswordResponse;
import hoangdung.vn.projectSpring.entity.PasswordReset;
import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.repository.PasswordResetRepository;
import hoangdung.vn.projectSpring.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    
    private final EmailService emailService;
    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createPasswordResetToken(String email) {
    User user = this.userRepository.findByEmail(email).orElseThrow(() -> 
            new RuntimeException("User not found with email: " + email));

    String token = UUID.randomUUID().toString();
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setToken(token);
        passwordReset.setUser(user);
        passwordReset.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        passwordResetRepository.save(passwordReset);

        String link = "http://localhost:8080/api/v1/reset/password?token=" + token;
        String subject = "Đặt lại mật khẩu";
        String body = "Xin chào " + user.getName() + ",\n\n"
                + "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng truy cập vào liên kết dưới đây để đặt lại:\n"
                + link + "\n\n"
                + "Liên kết có hiệu lực trong 30 phút.";

        emailService.sendMail(user.getEmail(), subject, body);

    }

    public ResetPasswordResponse resetPassword(String token, String newPassword) {

        PasswordReset passwordReset = passwordResetRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

        if (passwordReset.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token đã hết hạn");
        }

        User user = passwordReset.getUser();
        user.setPassword(passwordEncoder.encode(newPassword)); // Nên mã hóa bằng BCrypt
        userRepository.save(user);

        // Xóa token nếu cần
        passwordResetRepository.delete(passwordReset);
        return new ResetPasswordResponse(user.getId(), user.getName(), user.getEmail());
    }

}

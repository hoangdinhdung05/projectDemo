package hoangdung.vn.projectSpring.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;

    public void sendMail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hoangdinhdung0205@gmail.com"); // giống username trong cấu hình
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

    }

}

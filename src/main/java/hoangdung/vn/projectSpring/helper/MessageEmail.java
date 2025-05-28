package hoangdung.vn.projectSpring.helper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import hoangdung.vn.projectSpring.entity.Discount;
import hoangdung.vn.projectSpring.entity.Order;
import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.service.EmailService;
import hoangdung.vn.projectSpring.service.InvoiceService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageEmail {
    
    private final EmailService emailService;
    private final InvoiceService invoiceService;

    public void sendOrderWithInvoice(Order order) {
        // Tính tổng gốc (trước giảm giá)
        BigDecimal totalOriginal = order.getOrderDetails().stream()
                .map(detail -> detail.getPrice().multiply(detail.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAfterDiscount = order.getTotalPrice();
        BigDecimal discountAmount = totalOriginal.subtract(totalAfterDiscount);
        Discount discount = order.getDiscount();

        // Soạn nội dung email
        StringBuilder body = new StringBuilder();
        body.append("Cảm ơn bạn đã đặt hàng tại HoangDungShop.\n\n");
        body.append("Thông tin đơn hàng:\n");
        body.append("Tổng giá gốc: ").append(totalOriginal).append(" VND\n");

        if (discount != null) {
            body.append("Mã giảm giá: ").append(discount.getCode()).append("\n");
            body.append("Số tiền giảm: ").append(discountAmount).append(" VND\n");
        }

        body.append("Tổng thanh toán: ").append(totalAfterDiscount).append(" VND\n\n");
        body.append("Vui lòng xem hóa đơn đính kèm.");

        // Tạo PDF hóa đơn
        byte[] pdf = invoiceService.generateInvoicePdf(order);

        // Gửi mail
        emailService.sendOrderConfirmationWithInvoice(
                order.getUser().getEmail(),
                "Đơn hàng #" + order.getId() + " đã đặt thành công!",
                body.toString(),
                pdf
        );
    }

    public void sendResetPasswordEmail(User user, String token) {
        String link = "http://localhost:8080/api/v1/reset/password?token=" + token;
        String subject = "Đặt lại mật khẩu";
        String body = "Xin chào " + user.getName() + ",\n\n"
                + "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng truy cập vào liên kết dưới đây để đặt lại:\n"
                + link + "\n\n"
                + "Liên kết có hiệu lực trong 30 phút.";

        emailService.sendMail(user.getEmail(), subject, body);
    }

}

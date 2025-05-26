package hoangdung.vn.projectSpring.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import hoangdung.vn.projectSpring.entity.Order;
import hoangdung.vn.projectSpring.entity.OrderDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    public byte[] generateInvoicePdf(Order order) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font bodyFont = new Font(Font.HELVETICA, 12);

            document.add(new Paragraph("HÓA ĐƠN MUA HÀNG", headerFont));
            document.add(new Paragraph("Mã đơn hàng: #" + order.getId(), bodyFont));
            document.add(new Paragraph("Ngày đặt: " + order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), bodyFont));
            document.add(new Paragraph("Khách hàng: " + order.getUser().getName(), bodyFont));
            document.add(new Paragraph("Địa chỉ giao hàng: " + order.getShippingAddress(), bodyFont));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Sản phẩm");
            table.addCell("Số lượng");
            table.addCell("Giá");
            table.addCell("Thành tiền");

            for (OrderDetail detail : order.getOrderDetails()) {
                table.addCell(detail.getProduct().getName());
                table.addCell(detail.getQuantity().toPlainString());
                table.addCell(detail.getPrice().toPlainString());
                table.addCell(detail.getPrice().multiply(detail.getQuantity()).toPlainString());
            }

            document.add(table);

            document.add(new Paragraph("\nTổng tiền: " + order.getTotalPrice().toPlainString() + " VND", bodyFont));
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating invoice PDF", e);
        }
    }
}

package hoangdung.vn.projectSpring.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalPrice;
    private String status;
    private String note;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderDetailResponse> items;
    private DiscountResponse discount;
}

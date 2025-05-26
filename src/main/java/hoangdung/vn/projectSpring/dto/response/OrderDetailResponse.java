package hoangdung.vn.projectSpring.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderDetailResponse {
    private Long id;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal price;
    private LocalDateTime createdAt;
}
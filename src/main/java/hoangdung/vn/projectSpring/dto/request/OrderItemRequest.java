package hoangdung.vn.projectSpring.dto.request;

import lombok.*;
import java.math.BigDecimal;

@Data
public class OrderItemRequest {
    private Long productId;
    private BigDecimal quantity;
}
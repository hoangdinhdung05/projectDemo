package hoangdung.vn.projectSpring.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DiscountResponse {
    private String code;
    private String description;
    private BigDecimal discountAmount;
    private boolean isPercentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}

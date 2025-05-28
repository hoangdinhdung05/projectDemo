package hoangdung.vn.projectSpring.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountRequest {
    private String code;
    private String description;
    private BigDecimal discountAmount;
    private boolean isPercentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxUsage;
    private boolean isActive;
}

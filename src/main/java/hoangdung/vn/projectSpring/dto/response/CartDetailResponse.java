package hoangdung.vn.projectSpring.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CartDetailResponse {
    private Long id;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal total;
    private LocalDateTime createdAt;

    public BigDecimal getTotal() {
        if (price != null && quantity != null) {
            return price.multiply(quantity);
        }
        return BigDecimal.ZERO;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}

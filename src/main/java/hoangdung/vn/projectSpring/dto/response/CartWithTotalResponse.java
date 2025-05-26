package hoangdung.vn.projectSpring.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartWithTotalResponse {
    private List<CartDetailResponse> items;
    private BigDecimal totalAmount;
}

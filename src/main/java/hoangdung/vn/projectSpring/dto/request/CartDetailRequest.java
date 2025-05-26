package hoangdung.vn.projectSpring.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartDetailRequest {
    @NotBlank(message = "Cart ID cannot be blank")
    private Long cartId;
    @NotNull(message = "Product ID cannot be null")
    private Long productId;
    @NotNull(message = "Quantity cannot be null")
    private BigDecimal quantity;
}
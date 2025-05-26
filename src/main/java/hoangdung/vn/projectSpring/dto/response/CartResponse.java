package hoangdung.vn.projectSpring.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CartResponse {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartDetailResponse> cartDetails;
}
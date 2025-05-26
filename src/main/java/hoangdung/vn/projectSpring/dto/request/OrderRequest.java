package hoangdung.vn.projectSpring.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private String note;
    private String shippingAddress;
    private List<OrderItemRequest> items; // danh sách sản phẩm
}

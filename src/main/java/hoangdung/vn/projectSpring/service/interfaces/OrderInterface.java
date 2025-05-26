package hoangdung.vn.projectSpring.service.interfaces;

import java.util.List;
import hoangdung.vn.projectSpring.dto.request.OrderRequest;
import hoangdung.vn.projectSpring.dto.response.OrderResponse;

public interface OrderInterface {
    
    // Define methods for order management
    OrderResponse createOrder(OrderRequest orderRequest);
    
    OrderResponse getOrderById(Long orderId);
    
    List<OrderResponse> getOrdersByUser(Long userId);
    
    void cancelOrder(Long orderId);
    
    OrderResponse updateOrderStatus(Long orderId, String newStatus); // thêm status mới
    
    // Note: The actual implementation of these methods would depend on the specific requirements and design of the application.

}

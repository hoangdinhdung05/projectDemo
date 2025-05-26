package hoangdung.vn.projectSpring.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hoangdung.vn.projectSpring.dto.request.OrderRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.OrderResponse;
import hoangdung.vn.projectSpring.service.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        ApiResponse<OrderResponse> response = ApiResponse.ok(orderResponse, "Order created successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        ApiResponse<OrderResponse> apiResponse = ApiResponse.ok(response, "Order retrieved successfully");
        return ResponseEntity.ok(apiResponse);
    }

    // Lấy danh sách đơn hàng của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUser(userId);
        ApiResponse<List<OrderResponse>> apiResponse = ApiResponse.ok(orders, "Orders retrieved successfully");
        return ResponseEntity.ok(apiResponse);
    }

    // Cập nhật trạng thái đơn hàng
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                                           @RequestParam String status) {
        OrderResponse response = orderService.updateOrderStatus(orderId, status);
        ApiResponse<OrderResponse> apiResponse = ApiResponse.ok(response, "Order status updated successfully");
        return ResponseEntity.ok(apiResponse);
    }

    // Hủy đơn hàng (chỉ hủy được đơn PENDING)
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

}

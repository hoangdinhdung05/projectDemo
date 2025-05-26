package hoangdung.vn.projectSpring.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hoangdung.vn.projectSpring.dto.request.OrderItemRequest;
import hoangdung.vn.projectSpring.dto.request.OrderRequest;
import hoangdung.vn.projectSpring.dto.response.OrderDetailResponse;
import hoangdung.vn.projectSpring.dto.response.OrderResponse;
import hoangdung.vn.projectSpring.entity.Order;
import hoangdung.vn.projectSpring.entity.OrderDetail;
import hoangdung.vn.projectSpring.entity.Product;
import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.repository.OrderDetailsRepository;
import hoangdung.vn.projectSpring.repository.OrderRepository;
import hoangdung.vn.projectSpring.repository.ProductRepository;
import hoangdung.vn.projectSpring.repository.UserRepository;
import hoangdung.vn.projectSpring.service.interfaces.OrderInterface;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderInterface {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InvoiceService invoiceService;
    private final EmailService mailService;

    // Mapping từ Order sang OrderResponse
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderDetailResponse> detailResponses = order.getOrderDetails()
                .stream()
                .map(detail -> OrderDetailResponse.builder()
                        .id(detail.getId())
                        .productId(detail.getProduct().getId())
                        .quantity(detail.getQuantity())
                        .price(detail.getPrice())
                        .createdAt(detail.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .note(order.getNote())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .items(detailResponses)
                .build();
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setNote(request.getNote());
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Tính tổng giá trị đơn hàng và lưu OrderDetail
        // Khởi tạo tổng giá trị đơn hàng
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            BigDecimal itemTotal = product.getPrice().multiply(item.getQuantity());
            totalPrice = totalPrice.add(itemTotal);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(product.getPrice());
            detail.setCreatedAt(LocalDateTime.now());

            orderDetails.add(detail);
        }

        order.setTotalPrice(totalPrice);
        order.setOrderDetails(orderDetails);


        Order savedOrder = orderRepository.save(order); // Cascade sẽ lưu cả OrderDetail nếu được cấu hình
        byte[] pdf = invoiceService.generateInvoicePdf(savedOrder);

        // Gửi mail kèm hóa đơn
        mailService.sendOrderConfirmationWithInvoice(
            savedOrder.getUser().getEmail(),
            "Đơn hàng #" + savedOrder.getId() + " đã đặt thành công!",
            "Cảm ơn bạn đã đặt hàng tại ShopABC.\nVui lòng xem hóa đơn đính kèm.",
            pdf
        );

        return mapToOrderResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (!order.getStatus().equalsIgnoreCase("PENDING")) {
            throw new RuntimeException("Only PENDING orders can be canceled.");
        }

        order.setStatus("CANCELED");
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        List<String> allowedStatuses = List.of("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELED");

        if (!allowedStatuses.contains(newStatus.toUpperCase())) {
            throw new RuntimeException("Invalid order status: " + newStatus);
        }

        order.setStatus(newStatus.toUpperCase());
        orderRepository.save(order);

        return mapToOrderResponse(order);
    }
}

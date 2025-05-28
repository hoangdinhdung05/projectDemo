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
import hoangdung.vn.projectSpring.entity.Discount;
import hoangdung.vn.projectSpring.entity.Order;
import hoangdung.vn.projectSpring.entity.OrderDetail;
import hoangdung.vn.projectSpring.entity.Product;
import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.helper.MessageEmail;
import hoangdung.vn.projectSpring.repository.OrderDetailsRepository;
import hoangdung.vn.projectSpring.repository.OrderRepository;
import hoangdung.vn.projectSpring.repository.ProductRepository;
import hoangdung.vn.projectSpring.repository.UserRepository;
import hoangdung.vn.projectSpring.service.interfaces.OrderInterface;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService implements OrderInterface {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InvoiceService invoiceService;
    private final EmailService mailService;
    private final DiscountService discountService;
    private final MessageEmail messageEmail;

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

    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Discount discount = null;
        if (request.getDiscountCode() != null && !request.getDiscountCode().isEmpty()) {
            discount = discountService.validateAndGetDiscount(request.getDiscountCode());
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            BigDecimal itemTotal = product.getPrice().multiply(item.getQuantity());
            totalPrice = totalPrice.add(itemTotal);

            OrderDetail detail = new OrderDetail();
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(product.getPrice());
            detail.setCreatedAt(LocalDateTime.now());

            orderDetails.add(detail);
        }

        BigDecimal totalAfterDiscount = discount != null
                ? discountService.applyDiscount(totalPrice, discount)
                : totalPrice;

        // BigDecimal discountAmount = totalPrice.subtract(totalAfterDiscount);

        Order order = new Order();
        order.setUser(user);
        order.setNote(request.getNote());
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(totalAfterDiscount);
        order.setDiscount(discount);
        order.setOrderDetails(orderDetails);

        orderDetails.forEach(detail -> detail.setOrder(order)); // thiết lập quan hệ 2 chiều

        Order savedOrder = orderRepository.save(order);

        if (discount != null) {
            discountService.increaseUsedCount(discount);
        }

        // byte[] pdf = invoiceService.generateInvoicePdf(savedOrder);

        // Soạn nội dung email
        // StringBuilder emailBody = new StringBuilder();
        // emailBody.append("Cảm ơn bạn đã đặt hàng tại HoangDungShop.\n\n");
        // emailBody.append("Thông tin đơn hàng:\n");
        // emailBody.append("Tổng giá gốc: ").append(totalPrice).append(" VND\n");

        // if (discount != null) {
        //     emailBody.append("Mã giảm giá: ").append(discount.getCode()).append("\n");
        //     emailBody.append("Số tiền giảm: ").append(discountAmount).append(" VND\n");
        // }

        // emailBody.append("Tổng thanh toán: ").append(totalAfterDiscount).append(" VND\n");
        // emailBody.append("\nVui lòng xem hóa đơn đính kèm.");

        this.messageEmail.sendOrderWithInvoice(savedOrder);

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

package hoangdung.vn.projectSpring.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import hoangdung.vn.projectSpring.dto.request.CartDetailRequest;
import hoangdung.vn.projectSpring.dto.response.CartDetailResponse;
import hoangdung.vn.projectSpring.dto.response.CartWithTotalResponse;
import hoangdung.vn.projectSpring.entity.Cart;
import hoangdung.vn.projectSpring.entity.CartDetail;
import hoangdung.vn.projectSpring.entity.Product;
import hoangdung.vn.projectSpring.repository.CartDetailsRepository;
import hoangdung.vn.projectSpring.repository.CartRepository;
import hoangdung.vn.projectSpring.repository.ProductRepository;
import hoangdung.vn.projectSpring.service.interfaces.CartDetailInterface;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartDetailService implements CartDetailInterface {

    private final CartDetailsRepository cartDetailRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    private CartDetailResponse mapToResponse(CartDetail detail) {
        return CartDetailResponse.builder()
                .id(detail.getId())
                .productId(detail.getProduct().getId())
                .quantity(detail.getQuantity())
                .price(detail.getPrice())
                .total(detail.getPrice().multiply(detail.getQuantity()))
                .createdAt(detail.getCreatedAt())
                .build();
    }

    @Override
    public CartDetailResponse addProductToCart(CartDetailRequest request) {
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + request.getCartId()));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        CartDetail cartDetail = cartDetailRepository
                .findByCartIdAndProductId(request.getCartId(), request.getProductId())
                .map(existingDetail -> {
                    existingDetail.setQuantity(existingDetail.getQuantity().add(request.getQuantity()));
                    return existingDetail;
                })
                .orElseGet(() -> {
                    CartDetail newDetail = new CartDetail();
                    newDetail.setCart(cart);
                    newDetail.setProduct(product);
                    newDetail.setQuantity(request.getQuantity());
                    newDetail.setPrice(product.getPrice());
                    return newDetail;
                });

        return mapToResponse(cartDetailRepository.save(cartDetail));
    }


    @Override
    public void removeProductFromCart(Long cartDetailId) {
        cartDetailRepository.deleteById(cartDetailId);
    }

    @Override
    public List<CartDetailResponse> getCartDetails(Long cartId) {
        List<CartDetail> details = cartDetailRepository.findByCartId(cartId);
        return details.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CartWithTotalResponse getCartWithTotal(Long cartId) {
        List<CartDetail> details = cartDetailRepository.findByCartId(cartId);

        if (details.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<CartDetailResponse> responses = details.stream().map(this::mapToResponse).collect(Collectors.toList());

        BigDecimal totalAmount = responses.stream()
                .map(CartDetailResponse::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartWithTotalResponse.builder()
                .items(responses)
                .totalAmount(totalAmount)
                .build();
    }

    // Optional: update quantity of product in cart
    public CartDetailResponse updateProductInCart(Long cartDetailId, CartDetailRequest request) {
        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new RuntimeException("Cart detail not found"));

        cartDetail.setQuantity(request.getQuantity());
        return mapToResponse(cartDetailRepository.save(cartDetail));
    }
}

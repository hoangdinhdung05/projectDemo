package hoangdung.vn.projectSpring.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import hoangdung.vn.projectSpring.dto.request.CartRequest;
import hoangdung.vn.projectSpring.dto.response.CartResponse;
import hoangdung.vn.projectSpring.entity.Cart;
import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.repository.CartRepository;
import hoangdung.vn.projectSpring.repository.UserRepository;
import hoangdung.vn.projectSpring.service.interfaces.CartInterface;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService implements CartInterface {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    //convert Cart entity to CartResponse DTO
    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());
        // mapping cartDetails sẽ thêm sau
        return response;
    }

    @Override
    public CartResponse createCart(CartRequest request) {
        
        //check user ứng với cart đó
        User user = this.userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
        
        //Nếu chưa có tạo cart mới
        Cart newCart = new Cart();
        newCart.setUser(user);
        this.cartRepository.save(newCart);
        //Trả về response
        return mapToCartResponse(newCart);
    }

    @Override
    public CartResponse getCartById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return mapToCartResponse(cart);
    }

    @Override
    public List<CartResponse> getCartsByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .stream()
                .map(this::mapToCartResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }
    
}

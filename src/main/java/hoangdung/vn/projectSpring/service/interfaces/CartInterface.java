package hoangdung.vn.projectSpring.service.interfaces;

import java.util.List;

import hoangdung.vn.projectSpring.dto.request.CartRequest;
import hoangdung.vn.projectSpring.dto.response.CartResponse;

public interface CartInterface {
    
    CartResponse createCart(CartRequest request);
    CartResponse getCartById(Long id);
    List<CartResponse> getCartsByUserId(Long userId);
    void deleteCart(Long id);

}

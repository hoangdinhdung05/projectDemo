package hoangdung.vn.projectSpring.service.interfaces;

import java.util.List;
import hoangdung.vn.projectSpring.dto.request.CartDetailRequest;
import hoangdung.vn.projectSpring.dto.response.CartDetailResponse;
import hoangdung.vn.projectSpring.dto.response.CartWithTotalResponse;

public interface CartDetailInterface {
    
    CartDetailResponse addProductToCart(CartDetailRequest request); // Thêm sản phẩm
    void removeProductFromCart(Long cartDetailId); // Xóa sản phẩm khỏi giỏ
    List<CartDetailResponse> getCartDetails(Long cartId); // Lấy danh sách sản phẩm trong giỏ
    CartWithTotalResponse getCartWithTotal(Long cartId); // Lấy giỏ hàng với tổng tiền
}

package hoangdung.vn.projectSpring.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hoangdung.vn.projectSpring.dto.request.CartDetailRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.CartDetailResponse;
import hoangdung.vn.projectSpring.service.CartDetailService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cart-details")
@RequiredArgsConstructor
public class CartDetailController {

    private final CartDetailService cartDetailService;

    @PostMapping("/add")
    public ResponseEntity<?> addProductToCart(@RequestBody CartDetailRequest request) {
        try {
            CartDetailResponse response = cartDetailService.addProductToCart(request);
            return ResponseEntity.ok(ApiResponse.ok(response, "Product added to cart successfully"));
        } catch (Exception e) {
            ApiResponse<?> response = ApiResponse.error("Error", e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/remove/{cartDetailId}")
    public ResponseEntity<?> removeProductFromCart(@PathVariable Long cartDetailId) {
        try {
            cartDetailService.removeProductFromCart(cartDetailId);
            return ResponseEntity.ok(ApiResponse.ok(null, "Product removed from cart successfully"));
        } catch (Exception e) {
            ApiResponse<?> response = ApiResponse.error("Error", e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<?> getCartDetails(@PathVariable Long cartId) {
        try {
            List<CartDetailResponse> response = cartDetailService.getCartDetails(cartId);
            return ResponseEntity.ok(ApiResponse.ok(response, "Cart details fetched successfully"));
        } catch (Exception e) {
            ApiResponse<?> response = ApiResponse.error("Error", e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/cart/{cartId}/total")
    public ResponseEntity<?> getCartWithTotal(@PathVariable Long cartId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(cartDetailService.getCartWithTotal(cartId), "Cart total fetched successfully"));
        } catch (Exception e) {
            ApiResponse<?> response = ApiResponse.error("Error", e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/update/{cartDetailId}")
    public ResponseEntity<?> updateProductInCart(@PathVariable Long cartDetailId, @RequestBody CartDetailRequest request) {
        try {
            CartDetailResponse response = cartDetailService.updateProductInCart(cartDetailId, request);
            return ResponseEntity.ok(ApiResponse.ok(response, "Product updated in cart successfully"));
        } catch (Exception e) {
            ApiResponse<?> response = ApiResponse.error("Error", e.getMessage(), HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(response);
        }
    }
}

package hoangdung.vn.projectSpring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hoangdung.vn.projectSpring.dto.request.CartRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.CartResponse;
import hoangdung.vn.projectSpring.service.CartService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;

    @PostMapping("/create")
    public ResponseEntity<?> createCart(@RequestBody CartRequest request) {
        try {

            CartResponse cartResponse = cartService.createCart(request);
            ApiResponse<CartResponse> response = ApiResponse.ok(cartResponse, "Cart created successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating cart: " + e.getMessage());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getCartById(@PathVariable Long id) {
        try {
            CartResponse cartResponse = cartService.getCartById(id);
            ApiResponse<CartResponse> response = ApiResponse.ok(cartResponse, "Cart retrieved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving cart: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCartsByUserId(@PathVariable Long userId) {
        try {
            if (userId == null) {
                return ResponseEntity.badRequest().body("User ID cannot be null");
            }
            if (userId <= 0) {
                return ResponseEntity.badRequest().body("User ID must be a positive number");
            }
            ApiResponse<?> response = ApiResponse.ok(cartService.getCartsByUserId(userId), "Carts retrieved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving carts: " + e.getMessage());
        }
    }
}

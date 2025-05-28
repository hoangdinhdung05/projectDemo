package hoangdung.vn.projectSpring.controller;

import hoangdung.vn.projectSpring.dto.request.DiscountRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.DiscountResponse;
import hoangdung.vn.projectSpring.entity.Discount;
import hoangdung.vn.projectSpring.service.DiscountService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DiscountResponse>> createDiscount(@RequestBody DiscountRequest request) {
        DiscountResponse response = discountService.createDiscount(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Discount created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiscountResponse>> getDiscount(@PathVariable Long id) {
        DiscountResponse response = discountService.getDiscount(id);
        return ResponseEntity.ok(ApiResponse.ok(response, "Discount retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DiscountResponse>>> getAllDiscounts() {
        List<DiscountResponse> discounts = discountService.getAllDiscounts();
        return ResponseEntity.ok(ApiResponse.ok(discounts, "All discounts retrieved successfully"));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse<DiscountResponse>> updateDiscount(@PathVariable Long id,
                                                                        @RequestBody DiscountRequest request) {
        DiscountResponse response = discountService.updateDiscount(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Discount updated successfully"));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.ok(ApiResponse.message("Discount deleted successfully", HttpStatus.OK));
    }

    // Ví dụ thêm API validate mã giảm giá (nếu cần)
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<DiscountResponse>> validateDiscount(@RequestParam String code) {
        Discount discount = discountService.validateAndGetDiscount(code);
        DiscountResponse response = DiscountResponse.builder()
                .code(discount.getCode())
                .description(discount.getDescription())
                .discountAmount(discount.getDiscountAmount())
                .isPercentage(discount.isPercentage())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response, "Discount code is valid"));
    }

    // Ví dụ API áp dụng mã giảm giá (trả về tổng tiền sau giảm)
    @GetMapping("/apply")
    public ResponseEntity<ApiResponse<BigDecimal>> applyDiscount(@RequestParam BigDecimal originalTotal,
                                                                 @RequestParam String code) {
        Discount discount = discountService.validateAndGetDiscount(code);
        BigDecimal totalAfterDiscount = discountService.applyDiscount(originalTotal, discount);
        return ResponseEntity.ok(ApiResponse.ok(totalAfterDiscount, "Discount applied successfully"));
    }
}

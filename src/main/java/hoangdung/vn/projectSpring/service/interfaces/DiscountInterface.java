package hoangdung.vn.projectSpring.service.interfaces;

import java.math.BigDecimal;
import java.util.List;

import hoangdung.vn.projectSpring.dto.request.DiscountRequest;
import hoangdung.vn.projectSpring.dto.response.DiscountResponse;
import hoangdung.vn.projectSpring.entity.Discount;

public interface DiscountInterface {

    DiscountResponse createDiscount(DiscountRequest discountRequest);
    DiscountResponse updateDiscount(Long id, DiscountRequest discountRequest);
    void deleteDiscount(Long id);
    DiscountResponse getDiscount(Long id);
    List<DiscountResponse> getAllDiscounts();
    Discount validateAndGetDiscount(String code);
    BigDecimal applyDiscount(BigDecimal originalTotal, Discount discount);
}
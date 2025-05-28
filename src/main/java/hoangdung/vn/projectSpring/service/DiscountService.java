package hoangdung.vn.projectSpring.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import hoangdung.vn.projectSpring.dto.request.DiscountRequest;
import hoangdung.vn.projectSpring.dto.response.DiscountResponse;
import hoangdung.vn.projectSpring.entity.Discount;
import hoangdung.vn.projectSpring.repository.DiscountRepository;
import hoangdung.vn.projectSpring.service.interfaces.DiscountInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional //add vào để đồng bộ với order khi gọi
public class DiscountService implements DiscountInterface {

    private final DiscountRepository discountRepository;

    private DiscountResponse toResponse(Discount discount) {
        return DiscountResponse.builder()
            .code(discount.getCode())
            .description(discount.getDescription())
            .discountAmount(discount.getDiscountAmount())
            .isPercentage(discount.isPercentage())
            .startDate(discount.getStartDate())
            .endDate(discount.getEndDate())
            .build();
    }

    @Override
    public DiscountResponse createDiscount(DiscountRequest request) {
        //check xem có tồn tại chưa
        if(this.discountRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Discount code already exists");
        }

        //create discount
        Discount discount = new Discount();
        discount.setCode(request.getCode());
        discount.setDescription(request.getDescription());
        discount.setDiscountAmount(request.getDiscountAmount());
        discount.setPercentage(request.isPercentage());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setMaxUsage(request.getMaxUsage());
        discount.setActive(request.isActive());

        discount = this.discountRepository.save(discount);
        return toResponse(discount);

    }

    public DiscountResponse updateDiscount(Long id, DiscountRequest request) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));

        discount.setCode(request.getCode());
        discount.setDescription(request.getDescription());
        discount.setDiscountAmount(request.getDiscountAmount());
        discount.setPercentage(request.isPercentage());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setMaxUsage(request.getMaxUsage());
        discount.setActive(request.isActive());

        discountRepository.save(discount);
        return toResponse(discount);
    }

    public void deleteDiscount(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        discountRepository.delete(discount);
    }

    @Override
    public DiscountResponse getDiscount(Long id) {
        return toResponse(discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found")));
    }

    public List<DiscountResponse> getAllDiscounts() {
        return discountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Discount validateAndGetDiscount(String code) {
        return discountRepository.findByCode(code)
                .filter(discount -> {
                    LocalDateTime now = LocalDateTime.now();
                    return discount.isActive() &&
                            (discount.getStartDate() == null || !now.isBefore(discount.getStartDate())) &&
                            (discount.getEndDate() == null || !now.isAfter(discount.getEndDate())) &&
                            (discount.getMaxUsage() == null || discount.getUsedCount() < discount.getMaxUsage());
                })
                .orElseThrow(() -> new RuntimeException("Discount code is invalid, expired or inactive"));
    }

    @Override
    public BigDecimal applyDiscount(BigDecimal originalTotal, Discount discount) {
        if (originalTotal == null || discount == null) return BigDecimal.ZERO;

        BigDecimal discountValue = discount.isPercentage()
                ? originalTotal.multiply(discount.getDiscountAmount()).divide(BigDecimal.valueOf(100))
                : discount.getDiscountAmount();

        BigDecimal discountedTotal = originalTotal.subtract(discountValue);
        return discountedTotal.max(BigDecimal.ZERO); // không bao giờ < 0
    }

    public void increaseUsedCount(Discount discount) {
        if (discount == null) return;
        
        if (discount.getMaxUsage() != null && discount.getUsedCount() >= discount.getMaxUsage()) {
            throw new RuntimeException("Discount usage limit exceeded");
        }

        discount.setUsedCount(discount.getUsedCount() + 1);
        discountRepository.save(discount);
    }


}

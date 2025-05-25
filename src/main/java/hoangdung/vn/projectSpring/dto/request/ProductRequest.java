package hoangdung.vn.projectSpring.dto.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String name;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private String thumbnail;
    private BigDecimal salePrice;
    private List<String> imageUrls;
    private Integer stockQuantity;
    private String sku;
    private Boolean isActive;
    private Boolean isHot;
    private Boolean isNew;
    private Long categoryId;
}

package hoangdung.vn.projectSpring.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description", length = 255)
    private String description;

    // Giảm theo phần trăm (10% = 10.00) hoặc số tiền cố định (tùy theo hệ thống bạn xây)
    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount;

    // true nếu giảm theo phần trăm, false nếu giảm số tiền cố định
    @Column(name = "is_percentage", nullable = false)
    private boolean isPercentage;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "max_usage")
    private Integer maxUsage;

    @Column(name = "used_count")
    private Integer usedCount = 0;

    @Column(name = "is_active")
    private boolean isActive = true;

    @PrePersist
    public void prePersist() {
        if (usedCount == null) usedCount = 0;
    }
}

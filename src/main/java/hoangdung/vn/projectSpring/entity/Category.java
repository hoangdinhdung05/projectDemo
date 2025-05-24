package hoangdung.vn.projectSpring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tương ứng AUTO_INCREMENT
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(columnDefinition = "BIT", nullable = false)
    private Boolean status = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Quan hệ phân cấp cha - con (self join)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> children;

    @Column(name = "is_hot", columnDefinition = "BIT", nullable = false)
    private Boolean isHot = false;

    @Column(name = "is_new", columnDefinition = "BIT", nullable = false)
    private Boolean isNew = false;

    @Column(length = 50)
    private String type;

    // Quan hệ với User cho created_by
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "created_by", nullable = false)
    // private User createdBy;

    // // Quan hệ với User cho updated_by
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "updated_by")
    // private User updatedBy;

    private String createdBy;
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // hashCode, equals nếu cần
}

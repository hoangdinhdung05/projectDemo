package hoangdung.vn.projectSpring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hoangdung.vn.projectSpring.entity.Product;
import hoangdung.vn.projectSpring.entity.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    void deleteByProduct(Product product);

}

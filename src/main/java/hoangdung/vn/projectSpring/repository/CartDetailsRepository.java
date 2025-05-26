package hoangdung.vn.projectSpring.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import hoangdung.vn.projectSpring.entity.CartDetail;

@Repository
public interface CartDetailsRepository extends JpaRepository<CartDetail, Long> {
    
    List<CartDetail> findByCartId(Long cartId);
    Optional<CartDetail> findByCartIdAndProductId(Long cartId, Long productId);

}

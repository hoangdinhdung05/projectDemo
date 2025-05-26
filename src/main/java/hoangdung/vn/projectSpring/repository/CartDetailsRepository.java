package hoangdung.vn.projectSpring.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import hoangdung.vn.projectSpring.entity.CartDetail;

@Repository
public interface CartDetailsRepository extends JpaRepository<CartDetail, Long> {
    
}

package hoangdung.vn.projectSpring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hoangdung.vn.projectSpring.entity.OrderDetail;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetail, Long> {
    
}

package hoangdung.vn.projectSpring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hoangdung.vn.projectSpring.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
}

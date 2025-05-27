package hoangdung.vn.projectSpring.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hoangdung.vn.projectSpring.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // Tìm kiếm permission theo tên
    Optional<Permission> findByName(String name);

    // Kiểm tra xem permission có tồn tại hay không
    boolean existsByName(String name);

    // Xóa permission theo tên
    void deleteByName(String name);
    
}

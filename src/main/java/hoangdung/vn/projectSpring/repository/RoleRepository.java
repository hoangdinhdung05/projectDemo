package hoangdung.vn.projectSpring.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import hoangdung.vn.projectSpring.entity.Role;
import java.util.List;
import java.util.Set;
import hoangdung.vn.projectSpring.entity.Permission;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Tìm kiếm role theo tên
    Optional<Role> findByName(String name);
    
    // Kiểm tra xem role có tồn tại hay không
    boolean existsByName(String name);
    
    // Xóa role theo tên
    void deleteByName(String name);

    List<Role> findByPermissions(Set<Permission> permissions);

    // Tìm tất cả Role có chứa ít nhất 1 trong các Permission truyền vào
    List<Role> findDistinctByPermissionsIn(Set<Permission> permissions);

    // Hoặc dùng JPQL để tìm role có tất cả permissions truyền vào (nếu cần)
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p IN :permissions GROUP BY r.id HAVING COUNT(p) = :permCount")
    List<Role> findByPermissionsExactMatch(@Param("permissions") Set<Permission> permissions, @Param("permCount") long permCount);
    
}

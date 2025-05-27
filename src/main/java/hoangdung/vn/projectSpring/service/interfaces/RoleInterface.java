package hoangdung.vn.projectSpring.service.interfaces;

import java.util.List;

import hoangdung.vn.projectSpring.dto.request.RoleRequest;
import hoangdung.vn.projectSpring.dto.response.RoleResponse;

public interface RoleInterface {
    
    // Define methods for role management, e.g., createRole, updateRole, deleteRole, getRoleById, etc.
    RoleResponse createRole(RoleRequest request);
    RoleResponse updateRole(Long id, RoleRequest request);
    void deleteRole(Long id);
    RoleResponse getRoleById(Long id);
    List<RoleResponse> getAllRoles();

}

package hoangdung.vn.projectSpring.service.interfaces;

import java.util.List;

import hoangdung.vn.projectSpring.dto.request.PermissionRequest;
import hoangdung.vn.projectSpring.dto.response.PermissionResponse;

public interface PermissionInterface {
    
    // Define methods for permission management, e.g., createPermission, updatePermission, deletePermission, getPermissionById, etc.
    // Example:
    PermissionResponse createPermission(PermissionRequest request);
    PermissionResponse updatePermission(Long id, PermissionRequest request);
    void deletePermission(Long id);
    PermissionResponse getPermissionById(Long id);
    List<PermissionResponse> getAllPermissions();

}

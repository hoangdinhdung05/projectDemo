package hoangdung.vn.projectSpring.service;

import hoangdung.vn.projectSpring.dto.request.PermissionRequest;
import hoangdung.vn.projectSpring.dto.response.PermissionResponse;
import hoangdung.vn.projectSpring.entity.Permission;
import hoangdung.vn.projectSpring.repository.PermissionRepository;
import hoangdung.vn.projectSpring.service.interfaces.PermissionInterface;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PermissionService implements PermissionInterface {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    private hoangdung.vn.projectSpring.dto.response.PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .build();
    }

    private Permission toPermission(PermissionRequest request) {
        return Permission.builder()
                .name(request.getName())
                .build();
    }

    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = toPermission(request);
        Permission saved = permissionRepository.save(permission);
        return toPermissionResponse(saved);
    }

    public PermissionResponse updatePermission(Long id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission không tồn tại"));

        permission.setName(request.getName());
        Permission updated = permissionRepository.save(permission);
        return toPermissionResponse(updated);
    }

    public PermissionResponse getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission không tồn tại"));
        return toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toPermissionResponse)
                .collect(Collectors.toList());
    }

    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }
}

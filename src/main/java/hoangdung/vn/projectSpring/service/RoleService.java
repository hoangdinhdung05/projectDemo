package hoangdung.vn.projectSpring.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import hoangdung.vn.projectSpring.dto.request.RoleRequest;
import hoangdung.vn.projectSpring.dto.response.PermissionResponse;
import hoangdung.vn.projectSpring.dto.response.RoleResponse;
import hoangdung.vn.projectSpring.entity.Permission;
import hoangdung.vn.projectSpring.entity.Role;
import hoangdung.vn.projectSpring.repository.PermissionRepository;
import hoangdung.vn.projectSpring.repository.RoleRepository;
import hoangdung.vn.projectSpring.service.interfaces.RoleInterface;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleInterface {
    
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    private PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .build();
    }

    private RoleResponse toRoleResponse(Role role) {
        Set<PermissionResponse> permissionResponses = new HashSet<>();
        if (role.getPermissions() != null) {
            permissionResponses = role.getPermissions().stream()
                    .map(this::toPermissionResponse)
                    .collect(Collectors.toSet());
        }
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .permissions(permissionResponses)
                .build();
    }

    private Role toRole(RoleRequest request, Set<Permission> permissions) {
        return Role.builder()
                .name(request.getName())
                .permissions(permissions)
                .build();
    }

@Override
    public RoleResponse createRole(RoleRequest request) {
        List<Permission> permissionList = permissionRepository.findAllById(request.getPermissionIds());
        Set<Permission> permissions = new HashSet<>(permissionList);
        Role role = toRole(request, permissions);
        Role savedRole = roleRepository.save(role);
        return toRoleResponse(savedRole);
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        List<Permission> permissionList = permissionRepository.findAllById(request.getPermissionIds());
        Set<Permission> permissions = new HashSet<>(permissionList);
        
        existingRole.setName(request.getName());
        existingRole.setPermissions(permissions);
        
        Role updatedRole = roleRepository.save(existingRole);
        return toRoleResponse(updatedRole);
        
    }

    @Override
    public void deleteRole(Long id) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        roleRepository.delete(existingRole);
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return toRoleResponse(existingRole);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toList());
    }



}

package hoangdung.vn.projectSpring.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hoangdung.vn.projectSpring.dto.request.RoleRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.RoleResponse;
import hoangdung.vn.projectSpring.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<?> createRole(@RequestBody RoleRequest roleRequest) {
        try {
            if (roleRequest.getName() == null || roleRequest.getName().isEmpty()) {
                return ResponseEntity.badRequest().body("Role name is required");
            }
            RoleResponse roleResponse = this.roleService.createRole(roleRequest);
            ApiResponse<RoleResponse> response = ApiResponse.ok(roleResponse, "Role created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        try {
            RoleResponse roleResponse = this.roleService.getRoleById(id);
            return ResponseEntity.ok(ApiResponse.ok(roleResponse, "Role fetched successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error", e.getLocalizedMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        try {
            List<RoleResponse> roles = this.roleService.getAllRoles();
            return ResponseEntity.ok(ApiResponse.ok(roles, "Roles fetched successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error", e.getLocalizedMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest roleRequest) {
        try {
            RoleResponse updatedRole = this.roleService.updateRole(id, roleRequest);
            return ResponseEntity.ok(ApiResponse.ok(updatedRole, "Role updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error", e.getLocalizedMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            this.roleService.deleteRole(id);
            return ResponseEntity.ok(ApiResponse.ok(null, "Role deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error", e.getLocalizedMessage(), HttpStatus.BAD_REQUEST));
        }
    }

}

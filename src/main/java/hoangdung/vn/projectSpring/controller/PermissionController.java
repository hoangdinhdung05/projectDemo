package hoangdung.vn.projectSpring.controller;

import hoangdung.vn.projectSpring.dto.request.PermissionRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.PermissionResponse;
import hoangdung.vn.projectSpring.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/create")
    public ResponseEntity<?> createPermission(@RequestBody PermissionRequest request) {
        try {
            if (request.getName() == null || request.getName().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.message("Error", HttpStatus.BAD_REQUEST));
            }
            PermissionResponse permission = permissionService.createPermission(request);
            return ResponseEntity.ok(ApiResponse.ok(permission, "Permission created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermission(@PathVariable Long id, @RequestBody PermissionRequest request) {
        try {
            PermissionResponse updated = permissionService.updatePermission(id, request);
            return ResponseEntity.ok(ApiResponse.ok(updated, "Permission updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPermissionById(@PathVariable Long id) {
        try {
            PermissionResponse permission = permissionService.getPermissionById(id);
            return ResponseEntity.ok(ApiResponse.ok(permission, "Permission fetched successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPermissions() {
        try {
            List<PermissionResponse> permissions = permissionService.getAllPermissions();
            return ResponseEntity.ok(ApiResponse.ok(permissions, "Permissions fetched successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.ok(ApiResponse.ok(null, "Permission deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}

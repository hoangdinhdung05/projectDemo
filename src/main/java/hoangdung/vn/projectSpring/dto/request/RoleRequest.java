package hoangdung.vn.projectSpring.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class RoleRequest {

    @NotBlank(message = "Tên vai trò không được để trống")
    private String name;

    private Set<Long> permissionIds; // ID quyền gán cho Role
}
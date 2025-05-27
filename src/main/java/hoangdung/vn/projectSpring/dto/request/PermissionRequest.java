package hoangdung.vn.projectSpring.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionRequest {
    @NotBlank(message = "Tên quyền không được để trống")
    private String name;
}
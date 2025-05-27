package hoangdung.vn.projectSpring.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class RoleResponse {
    private Long id;
    private String name;
    private Set<PermissionResponse> permissions;
}
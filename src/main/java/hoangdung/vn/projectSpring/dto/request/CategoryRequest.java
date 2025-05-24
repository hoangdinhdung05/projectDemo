package hoangdung.vn.projectSpring.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    @NotBlank(message = "Status is required")
    private Boolean status;
    private Long parentId;

}

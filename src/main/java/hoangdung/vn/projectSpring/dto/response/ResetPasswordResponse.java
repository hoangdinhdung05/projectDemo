package hoangdung.vn.projectSpring.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordResponse {
    
    private long id;
    private String name;
    private String email;
    
}

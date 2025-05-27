package hoangdung.vn.projectSpring.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    
    private long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private List<Long> roleIds;
    // private String image;
    // private String provider;
    // private String providerId;

}

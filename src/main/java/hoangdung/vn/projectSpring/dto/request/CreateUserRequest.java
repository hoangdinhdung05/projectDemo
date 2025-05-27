package hoangdung.vn.projectSpring.dto.request;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private Set<Long> roleIds;
}
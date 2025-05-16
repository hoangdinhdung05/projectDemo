package hoangdung.vn.projectSpring.dto.response;

import hoangdung.vn.projectSpring.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private UserDTO user;
}

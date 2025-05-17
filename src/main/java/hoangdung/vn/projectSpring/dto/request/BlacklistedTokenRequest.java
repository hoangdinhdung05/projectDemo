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
public class BlacklistedTokenRequest {
    @NotBlank(message = "Token không được để trống")
    private String token;
}

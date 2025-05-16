package hoangdung.vn.projectSpring.dto.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String message;
    //dùng key-value để lưu các lỗi
    private Map<String, String> errors;

    


    
}

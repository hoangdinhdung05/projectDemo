package hoangdung.vn.projectSpring.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResponse<T> {
    
    private String message;
    private T data;

    public SuccessResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

}

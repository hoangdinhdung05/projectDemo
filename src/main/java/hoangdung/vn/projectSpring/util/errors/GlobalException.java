package hoangdung.vn.projectSpring.util.errors;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import hoangdung.vn.projectSpring.dto.response.ErrorResponse;

//đánh dấu để xử lí các lỗi toàn cục (anotation @ControllerAdvice)
@ControllerAdvice
public class GlobalException {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)

    public ResponseEntity<Object> handleValidException(MethodArgumentNotValidException ex) {
        System.out.println("Chị THU THANH xinh quá");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse("Có vấn đề khi validate dữ liệu", errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}


// {
//     message: "Có vấn đề khi validate dữ liệu",
//     errors: {
//         ...
//     }
// }
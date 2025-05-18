package hoangdung.vn.projectSpring.dto.response;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ApiResponse is a generic class that represents the structure of an API response.
 * It contains fields for success status, message, data, HTTP status, timestamp, and error details.
 *
 * @param <T> the type of the data field
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private HttpStatus status;
    private LocalDateTime timestamp;
    private ErrorResource error;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonInclude
    public static class ErrorResource {
        private String code;
        private String message;
        private String details;
        
        public ErrorResource(String message) {
            this.message = message;
        }
        
        public ErrorResource(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public ErrorResource(String code, String message, String details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }
    }

    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    //Tạo lớp builder cho ApiResponse thay vì dùng constructor
    public static <T> Builders<T> builder() {
        return new Builders<>();
    }


    public static class Builders<T> {

        private final ApiResponse<T> resource;

        private Builders() {
            this.resource = new ApiResponse<>();
        }

        public Builders<T> success(boolean success) {
            this.resource.success = success;
            return this;
        }

        public Builders<T> message(String message) {
            this.resource.message = message;
            return this;
        }

        public Builders<T> data(T data) {
            this.resource.data = data;
            return this;
        }

        public Builders<T> status(HttpStatus status) {
            this.resource.status = status;
            return this;
        }

        public Builders<T> error(ErrorResource error) {
            this.resource.error = error;
            return this;
        }

        public ApiResponse<T> build() {
            return this.resource;
        }
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .status(HttpStatus.OK)
                .build();
    }

    public static <T> ApiResponse<T> message(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .status(status)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(new ErrorResource(code, message))
                .message(message)
                .status(status)
                .build();
    }
}

//có data
// {
//     "success": boolean,
//     "message": string,
//     "data": {
//         "id": 1,
//         "name": "John Doe",
//         "email": 
//     },

//     "status": 200,...,
//     "timestamp": ... 
// }

// //chỉ có message
// {
//     "success": boolean,
//     "message": string,
//     "status": 200,...,
//     "timestamp": ... 
// }

// //error
// {
//     "success": boolean,
//     "status": 422,...,
//     "error": {
//         "message": string,
//         "errors": {
//             "email": [
//                 "Email is required",
//                 "Email is invalid"
//             ],
//             "password": [
//                 "Password is required"
//             ]
//         }
//     },
//     "timestamp": ... 
// }
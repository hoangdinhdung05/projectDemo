package hoangdung.vn.projectSpring.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hoangdung.vn.projectSpring.dto.request.CreateUserRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.CreateUserResponse;
import hoangdung.vn.projectSpring.dto.response.UserResponse;
import hoangdung.vn.projectSpring.service.UserService;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    // private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest newUser) {
        try {
            System.out.println("CREATE 1:");
            CreateUserResponse user = userService.createUser(newUser);
            ApiResponse<CreateUserResponse> response = ApiResponse.ok(user, "Success");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("create_user_failed", e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("server_error", "Đã xảy ra lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @GetMapping("/list")
    public ResponseEntity<?> getAllUsers() {
        ApiResponse<List<UserResponse>> response = ApiResponse.ok(userService.getAllUsers(), "Success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserResponse user = this.userService.getUserById(id);
        ApiResponse<UserResponse> response = ApiResponse.ok(user, "Success");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return ResponseEntity.ok().body(null);
        // System.out.println("Xóa tài khoản thành công với id: " + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(@RequestBody CreateUserRequest user) {
        ApiResponse<UserResponse> response = ApiResponse.ok(this.userService.updateUserById(user), "Success");
        return ResponseEntity.ok(response);  
    }
}

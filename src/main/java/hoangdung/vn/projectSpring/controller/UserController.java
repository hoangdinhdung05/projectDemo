package hoangdung.vn.projectSpring.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.service.UserService;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @PostMapping("/create")
    // public ResponseEntity<User> createUser(@RequestBody User newUser) {
    //     try {

    //         User user = userService.handleCreateUser(newUser);
    //         return ResponseEntity.ok(user);

    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().build();
    //     }
    // }

    // @GetMapping("/user")
    // public ResponseEntity<List<User>> getAllUsers() {
    //     return ResponseEntity.ok(this.userService.getAllUsers());
    // }

    // @GetMapping("/{id}")
    // public ResponseEntity<User> getUserById(@PathVariable Long id) {
    //     return ResponseEntity.ok(this.userService.getUserById(id));
    // }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    //     return ResponseEntity.ok().body(null);
    //     // System.out.println("Xóa tài khoản thành công với id: " + id);
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<User> updateUserById(@RequestBody User user) {
    //     return ResponseEntity.ok(this.userService.updateUserById(user));
    // }


}

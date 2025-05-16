package hoangdung.vn.projectSpring.service.interfaces;

import java.util.List;

import hoangdung.vn.projectSpring.dto.request.LoginRequest;
import hoangdung.vn.projectSpring.dto.response.LoginResponse;
import hoangdung.vn.projectSpring.entity.User;

public interface UserInterface {

    Object authenticate(LoginRequest request);
    // User handleCreateUser(User user);
    // void deleteUserById(Long id);
    // User getUserById(Long id);
    // User updateUserById(User user);
    // List<User> getAllUsers();

}

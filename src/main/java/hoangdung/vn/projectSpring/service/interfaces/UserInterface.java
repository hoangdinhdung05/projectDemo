package hoangdung.vn.projectSpring.service.interfaces;

import hoangdung.vn.projectSpring.dto.request.LoginRequest;

public interface UserInterface {

    Object authenticate(LoginRequest request);
    // User handleCreateUser(User user);
    // void deleteUserById(Long id);
    // User getUserById(Long id);
    // User updateUserById(User user);
    // List<User> getAllUsers();

}

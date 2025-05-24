package hoangdung.vn.projectSpring.service.interfaces;

import hoangdung.vn.projectSpring.dto.request.CreateUserRequest;
import hoangdung.vn.projectSpring.dto.request.LoginRequest;
import hoangdung.vn.projectSpring.dto.response.CreateUserResponse;
import hoangdung.vn.projectSpring.dto.response.UserResponse;
import java.util.List;

public interface UserInterface {

    Object authenticate(LoginRequest request);
    CreateUserResponse createUser(CreateUserRequest user);
    void deleteUserById(Long id);
    UserResponse getUserById(Long id);
    UserResponse updateUserById(CreateUserRequest user);
    List<UserResponse> getAllUsers();

}

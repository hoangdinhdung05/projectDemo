package hoangdung.vn.projectSpring.service;

import hoangdung.vn.projectSpring.dto.UserDTO;
import hoangdung.vn.projectSpring.dto.request.LoginRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.LoginResponse;
import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.repository.UserRepository;
import hoangdung.vn.projectSpring.service.interfaces.UserInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
// @AllArgsConstructor
public class UserService implements UserInterface {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final Long defaultExpirationTime;

    public UserService(
        UserRepository userRepository,
        JwtService jwtService,
        PasswordEncoder passwordEncoder,
        @Value("${jwt.defaultExpiration}") Long defaultExpirationTime
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.defaultExpirationTime = defaultExpirationTime;
    }

    @Override
    public Object authenticate(LoginRequest request) {
        try {

            //check user exist
            User user = this.userRepository.findByEmail(request.getEmail()).orElseThrow(() -> 
                    new BadCredentialsException("Tài khoản hoặc mật khẩu không đúng"));

            //check password
            if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Tài khoản hoặc mật khẩu không đúng");
            }

            //check true => create token || login success
            UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), user.getName());
            // create token
            String token = this.jwtService.generateToken(userDTO.getId(), userDTO.getEmail(), defaultExpirationTime);
            //create refresh token
            String refreshToken = this.jwtService.generateRefreshToken(userDTO.getId(), userDTO.getEmail());
            //return response
            return new LoginResponse(token, refreshToken, userDTO);

        } catch (BadCredentialsException e) {
            // Map<String, String> errors = new HashMap<>();
            // errors.put("message", e.getMessage());
            // ErrorResponse errorResponse = new ErrorResponse("Lỗi xác thực", errors);
            // return errorResponse;
            return ApiResponse.error("Auth_error", e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
        

    // @Override
    // public User handleCreateUser(User user) {
    //     System.out.println("handleCreateUser");
    //     return userRepository.save(user);
    // }

    // @Override
    // public void deleteUserById(Long id) {
    //     this.userRepository.deleteById(id);
    // }

    // @Override
    // public User getUserById(Long id) {
    //     try {

    //         Optional<User> user = this.userRepository.findById(id);
    //         if (user.isPresent()) {
    //             return user.get();
    //         } else {
    //             throw new RuntimeException("Không tìm thấy người dùng với id: " + id);
    //         }

    //     } catch (Exception e) {
    //         throw new RuntimeException("Có vấn đề xãy ra");
    //     }
    // }

    // @Override
    // public User updateUserById(User reqUser) {
    //     try {
    //         User currentUser = getUserById(reqUser.getId());
    //         if (currentUser != null) {
    //             currentUser.setEmail(reqUser.getEmail());
    //             currentUser.setUsername(reqUser.getUsername());
    //             currentUser.setPassword(reqUser.getPassword());
    //             return this.userRepository.save(currentUser);
    //         }
    //         return null;
    //     } catch (Exception e) {
    //         throw new RuntimeException("Có vấn đề xảy ra khi cập nhật user");
    //     }
    // }

    // @Override
    // public List<User> getAllUsers() {
    //     try {
    //         return this.userRepository.findAll();
    //     } catch (Exception e) {
    //         throw new RuntimeException("Có vấn đề xãy ra");
    //     }
    // }

}

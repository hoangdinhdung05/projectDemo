package hoangdung.vn.projectSpring.service;

import hoangdung.vn.projectSpring.dto.UserDTO;
import hoangdung.vn.projectSpring.dto.request.CreateUserRequest;
import hoangdung.vn.projectSpring.dto.request.LoginRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.CreateUserResponse;
import hoangdung.vn.projectSpring.dto.response.LoginResponse;
import hoangdung.vn.projectSpring.dto.response.UserResponse;
import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.repository.UserRepository;
import hoangdung.vn.projectSpring.service.interfaces.UserInterface;
import java.util.List;
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

    // private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    public UserService(
        UserRepository userRepository,
        JwtService jwtService,
        PasswordEncoder passwordEncoder,
        @Value("${jwt.defaultExpiration}") Long defaultExpirationTime) {

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

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {

        // logger.info("Creating user: {}", request);

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống");
        }   
        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .address(request.getAddress())
            .password(passwordEncoder.encode(request.getPassword())) // default password
            .build();

        User saveUser = this.userRepository.save(user);
        return new CreateUserResponse(
                saveUser.getId(),
                saveUser.getName(),
                saveUser.getEmail(),
                saveUser.getPhone(),
                saveUser.getAddress()
        );
    }

    @Override
    public void deleteUserById(Long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> 
                new RuntimeException("User not found with id: " + id));
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress()
        );
    }

    @Override
    public UserResponse updateUserById(CreateUserRequest request) {

        User user = this.userRepository.findById(request.getId()).orElseThrow(() -> 
                new RuntimeException("User not found with id: " + request.getId()));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        User updatedUser = this.userRepository.save(user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getAddress()
        );

    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> listUser = this.userRepository.findAll();
        List<UserResponse> listUserResponses = listUser.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getAddress()  
                ))
                .toList();
        return listUserResponses;
    }
        
}

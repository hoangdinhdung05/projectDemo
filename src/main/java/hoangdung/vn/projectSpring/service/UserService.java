package hoangdung.vn.projectSpring.service;

import hoangdung.vn.projectSpring.dto.UserDTO;
import hoangdung.vn.projectSpring.dto.request.CreateUserRequest;
import hoangdung.vn.projectSpring.dto.request.LoginRequest;
import hoangdung.vn.projectSpring.dto.response.ApiResponse;
import hoangdung.vn.projectSpring.dto.response.CreateUserResponse;
import hoangdung.vn.projectSpring.dto.response.LoginResponse;
import hoangdung.vn.projectSpring.dto.response.UserResponse;
import hoangdung.vn.projectSpring.entity.Role;
import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.repository.RoleRepository;
import hoangdung.vn.projectSpring.repository.UserRepository;
import hoangdung.vn.projectSpring.service.interfaces.UserInterface;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final RoleRepository roleRepository;
    private final Long defaultExpirationTime;

    // private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    public UserService(
        UserRepository userRepository,
        JwtService jwtService,
        PasswordEncoder passwordEncoder,
        RoleRepository roleRepository,
        @Value("${jwt.defaultExpiration}") Long defaultExpirationTime) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.defaultExpirationTime = defaultExpirationTime;
        this.roleRepository = roleRepository;
    }

    @Override
    public Object authenticate(LoginRequest request) {
        try {
            // 1. Lấy user theo email
            User user = this.userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Tài khoản hoặc mật khẩu không đúng"));

            // 2. Kiểm tra password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Tài khoản hoặc mật khẩu không đúng");
            }

            // 3. Lấy danh sách roles (String)
            // Set<Role> roles = user.getRoles();
            // List<String> roleNames = roles.stream()
            //     .map(Role::getName)
            //     .collect(Collectors.toList());

            // // 4. (Optional) Lấy danh sách permissions (String)
            // Set<String> permissions = roles.stream()
            //     .flatMap(role -> role.getPermissions().stream())
            //     .map(Permission::getName)
            //     .collect(Collectors.toSet());

            // 5. Tạo UserDTO để trả về client
            UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), user.getName());

            // 6. Tạo token JWT, truyền roles và permissions vào claim
            String token = this.jwtService.generateToken(
                    userDTO.getId(), 
                    userDTO.getEmail(), 
                    user.getRoles(), 
                    defaultExpirationTime
            );

            // 7. Tạo refresh token (có thể không cần roles/permissions)
            String refreshToken = this.jwtService.generateRefreshToken(userDTO.getId(), userDTO.getEmail());

            // 8. Trả về response
            return new LoginResponse(token, refreshToken, userDTO);

        } catch (BadCredentialsException e) {
            return ApiResponse.error("Auth_error", e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }


    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {

        // logger.info("Creating user: {}", request);

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống");
        }   

        Set<Role> roles = new HashSet<>();
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            roles = request.getRoleIds().stream()
                    .map(id -> roleRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Role id không tồn tại: " + id)))
                    .collect(Collectors.toSet());
        }

        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .address(request.getAddress())
            .password(passwordEncoder.encode(request.getPassword())) // default password
            .roles(roles)
            .build();

        User saveUser = this.userRepository.save(user);
        System.out.println("ROLEUSER: " + saveUser.getRoles());
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
                user.getAddress(),
                user.getRoles().stream()
                        .map(role -> role.getId()).toList()

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
                updatedUser.getAddress(),
                updatedUser.getRoles().stream()
                        .map(role -> role.getId()).toList()
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
                        user.getAddress(),
                        user.getRoles().stream()
                                .map(role -> role.getId()).toList()
                ))
                .toList();
        return listUserResponses;
    }
        
}

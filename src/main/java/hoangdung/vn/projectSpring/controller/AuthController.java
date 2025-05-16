package hoangdung.vn.projectSpring.controller;

import hoangdung.vn.projectSpring.dto.request.LoginRequest;
import hoangdung.vn.projectSpring.dto.response.ErrorResponse;
import hoangdung.vn.projectSpring.dto.response.LoginResponse;
import hoangdung.vn.projectSpring.service.interfaces.UserInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final UserInterface userInterface;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        
        Object result = this.userInterface.authenticate(request);
        if(result instanceof LoginResponse loginResponse) {
            return ResponseEntity.ok(loginResponse);
        } 

        if(result instanceof ErrorResponse errorResponse) {
            return ResponseEntity.unprocessableEntity().body(errorResponse);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Network error");

    }

}

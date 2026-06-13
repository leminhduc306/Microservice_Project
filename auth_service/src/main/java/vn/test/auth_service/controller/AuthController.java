package vn.test.auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.test.auth_service.dto.request.LoginDto;
import vn.test.auth_service.dto.request.UserRegistrationDto;
import vn.test.auth_service.dto.response.LoginResponse;
import vn.test.auth_service.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto dto) {
        userService.createUser(dto);
        return ResponseEntity.ok("Nguoi dung da duoc tao thanh cong");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDto dto) {
        return ResponseEntity.ok(userService.login(dto));
    }
}

package vn.test.auth_service.service;

import vn.test.auth_service.dto.request.LoginDto;
import vn.test.auth_service.dto.request.UserRegistrationDto;
import vn.test.auth_service.dto.response.LoginResponse;

public interface UserService {

    void createUser(UserRegistrationDto dto);

    LoginResponse login(LoginDto dto);
}

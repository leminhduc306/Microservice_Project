package vn.test.auth_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}

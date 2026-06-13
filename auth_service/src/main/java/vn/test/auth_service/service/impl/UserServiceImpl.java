package vn.test.auth_service.service.impl;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.test.auth_service.dto.request.LoginDto;
import vn.test.auth_service.dto.request.UserRegistrationDto;
import vn.test.auth_service.dto.response.LoginResponse;
import vn.test.auth_service.service.UserService;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Keycloak keycloak;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Override
    public void createUser(UserRegistrationDto dto) {
        var users = keycloak.realm(realm).users();

        try {
            if (!users.searchByUsername(dto.getUsername(), true).isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Username da ton tai"
                );
            }

            if (!users.searchByEmail(dto.getEmail(), true).isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Email da ton tai"
                );
            }
        } catch (ForbiddenException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Keycloak client chua co role query-users",
                    exception
            );
        }

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(dto.getPassword());
        user.setCredentials(Collections.singletonList(credential));

        try (Response response = users.create(user)) {
            if (response.getStatus() != 201) {
                String errorMessage = response.readEntity(String.class);
                log.error("Create user error {}", errorMessage);

                if (response.getStatus() == 409) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Username hoac email da ton tai"
                    );
                }

                throw new RuntimeException("Khong the tao user trong Keycloak");
            }
        }
    }

    @Override
    public LoginResponse login(LoginDto dto) {
        try (Keycloak loginClient = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build()) {
            AccessTokenResponse token = loginClient.tokenManager().getAccessToken();

            return LoginResponse.builder()
                    .accessToken(token.getToken())
                    .refreshToken(token.getRefreshToken())
                    .expiresIn(token.getExpiresIn())
                    .tokenType(token.getTokenType())
                    .build();
        } catch (Exception exception) {
            log.error("Login error", exception);
            throw new RuntimeException("Username hoac password khong dung");
        }
    }
}

package org.example.smidatesttask.serviceTests;

import org.example.smidatesttask.dto.AuthenticationRequest;
import org.example.smidatesttask.dto.AuthenticationResponse;
import org.example.smidatesttask.dto.RegisterRequest;
import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.model.User;
import org.example.smidatesttask.repository.UserRepository;
import org.example.smidatesttask.service.AuthenticationService;
import org.example.smidatesttask.service.JwtService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthenticationService tests
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class AuthenticationServiceTest {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    /**
     * set up the test user
     */
    @Before
    public void setUp() {
        testUser = new User();
        testUser.setEmail("test_user@com.ua");
        testUser.setPassword("supersecret!");
    }

    /**
     * tear down the test user
     */
    @After
    public void tearDown() {
        if (testUser != null && testUser.getEmail() != null) {
            testUser = userRepository.findUserByEmail(testUser.getEmail()).orElse(null);
            if (testUser != null) {
                userRepository.delete(testUser);
            }
        }
    }

    /**
     * register the valid user test
     */
    @Test
    public void validUser_register_thenAssertReturnTheToken() {
        RegisterRequest request = RegisterRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        AuthenticationResponse authenticationResponse;

        try {
            authenticationResponse = authenticationService.register(request);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        assertNotNull(authenticationResponse);
        assertNotNull(authenticationResponse.getToken());
        assertTrue(jwtService.isTokenValid(
                authenticationResponse.getToken(),
                userDetailsService.loadUserByUsername(request.getEmail()))
        );
    }

    /**
     * register the invalid user with taken email test
     */
    @Test
    public void invalidUserWithTakenEmail_tryToRegister_thenAssertRuntimeExceptionWithMessage() {
        userRepository.save(testUser);

        RegisterRequest request = RegisterRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        RuntimeException exception;

        try {
            exception = assertThrows(RuntimeException.class, () -> authenticationService.register(request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String errorMessage = exception.getMessage();

        assertEquals(errorMessage, "This email is taken already");
    }

    /**
     * register the invalid user with invalid data test
     */
    @Test
    public void invalidUserWithInvalidData_tryToRegister_thenAssertValidationExceptionWithMessage() {
        testUser.setEmail("not email");
        testUser.setPassword(null);

        RegisterRequest request = RegisterRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        ValidationException exception;

        try {
            exception = assertThrows(ValidationException.class, () -> authenticationService.register(request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String errorMessage = exception.getViolations().toString();

        assertTrue(errorMessage.contains("property=password"));
        assertTrue(errorMessage.contains("message=Password is required"));
        assertTrue(errorMessage.contains("property=email"));
        assertTrue(errorMessage.contains("message=Email is required"));
    }

    /**
     * authenticate the valid user test
     */
    @Test
    public void validUser_authenticate_thenAssertReturnTheToken() {
        // register the test user
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        try {
            authenticationService.register(registerRequest);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        // auth test
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        AuthenticationResponse authenticationResponse;

        try {
            authenticationResponse = authenticationService.authenticate(request);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        assertNotNull(authenticationResponse);
        assertNotNull(authenticationResponse.getToken());
        assertTrue(jwtService.isTokenValid(
                authenticationResponse.getToken(),
                userDetailsService.loadUserByUsername(request.getEmail()))
        );
    }

    /**
     * authenticate the user with invalid data test
     */
    @Test
    public void invalidUser_tryToAuthenticate_thenAssertValidationExceptionWithMessage() {
        testUser.setEmail("not email");
        testUser.setPassword(null);

        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        ValidationException exception;

        try {
            exception = assertThrows(ValidationException.class, () -> authenticationService.authenticate(request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String errorMessage = exception.getViolations().toString();

        assertTrue(errorMessage.contains("property=password"));
        assertTrue(errorMessage.contains("message=Password is required"));
        assertTrue(errorMessage.contains("property=email"));
        assertTrue(errorMessage.contains("message=Email is required"));
    }

    /**
     * authenticate the not register user test
     */
    @Test
    public void notRegisterUser_tryToAuthenticate_thenAssertRuntimeExceptionWithMessage() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        RuntimeException exception;

        try {
            exception = assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String errorMessage = exception.getMessage();

        assertEquals(errorMessage, "User not found");
    }

    /**
     * authenticate the user with invalid password test
     */
    @Test
    public void invalidUserWithInvalidPassword_tryToAuthenticate_thenAssertRuntimeExceptionWithMessage() {
        // register the test user
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        try {
            authenticationService.register(registerRequest);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        // auth test
        testUser.setPassword("invalid password");

        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        RuntimeException exception;

        try {
            exception = assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String errorMessage = exception.getMessage();

        assertEquals(errorMessage, "Bad credentials");
    }
}

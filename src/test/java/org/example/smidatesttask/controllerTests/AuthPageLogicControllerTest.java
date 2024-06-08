package org.example.smidatesttask.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.smidatesttask.dto.AuthenticationRequest;
import org.example.smidatesttask.dto.RegisterRequest;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthPageLogicController tests
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class AuthPageLogicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void validUser_register_thenAssertReturnTheToken() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertNotNull(response);
        Map<String, String> responseMap = objectMapper.readValue(response, Map.class);
        assertTrue(responseMap.containsKey("token"));
        assertTrue(jwtService.isTokenValid(
                responseMap.get("token"),
                userDetailsService.loadUserByUsername(testUser.getEmail()))
        );
    }

    /**
     * register the invalid user with taken email test
     */
    @Test
    public void invalidUserWithTakenEmail_tryToRegister_thenAssertRuntimeExceptionWithMessage() throws Exception {
        userRepository.save(testUser);

        RegisterRequest request = RegisterRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertEquals(response, "This email is taken already");
    }

    /**
     * register the invalid user with invalid data test
     */
    @Test
    public void invalidUserWithInvalidData_tryToRegister_thenAssertValidationExceptionWithMessage() throws Exception {
        testUser.setEmail("not email");
        testUser.setPassword(null);

        RegisterRequest request = RegisterRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertNotNull(response);
        assertFalse(response.contains("token"));
        assertTrue(response.contains("\"property\":\"password\""));
        assertTrue(response.contains("\"message\":\"Password is required\""));
        assertTrue(response.contains("\"property\":\"email\""));
        assertTrue(response.contains("\"message\":\"Email is required\""));
    }

    /**
     * authenticate the valid user test
     */
    @Test
    public void validUser_authenticate_thenAssertReturnTheToken() throws Exception {
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

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertNotNull(response);
        Map<String, String> responseMap = objectMapper.readValue(response, Map.class);
        assertTrue(responseMap.containsKey("token"));
        assertTrue(jwtService.isTokenValid(
                responseMap.get("token"),
                userDetailsService.loadUserByUsername(testUser.getEmail()))
        );
    }

    /**
     * authenticate the user with invalid data test
     */
    @Test
    public void invalidUser_tryToAuthenticate_thenAssertValidationExceptionWithMessage() throws Exception {
        testUser.setEmail("not email");
        testUser.setPassword(null);

        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertNotNull(response);
        assertFalse(response.contains("token"));
        assertTrue(response.contains("\"property\":\"password\""));
        assertTrue(response.contains("\"message\":\"Password is required\""));
        assertTrue(response.contains("\"property\":\"email\""));
        assertTrue(response.contains("\"message\":\"Email is required\""));
    }

    /**
     * authenticate the not register user test
     */
    @Test
    public void notRegisterUser_tryToAuthenticate_thenAssertRuntimeExceptionWithMessage() throws Exception {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertNotNull(response);
        assertEquals(response, "User not found");
    }

    /**
     * authenticate the user with invalid password test
     */
    @Test
    public void invalidUserWithInvalidPassword_tryToAuthenticate_thenAssertRuntimeExceptionWithMessage() throws Exception {
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

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertNotNull(response);
        assertEquals(response, "Bad credentials");
    }
}

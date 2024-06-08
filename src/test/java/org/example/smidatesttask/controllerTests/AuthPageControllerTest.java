package org.example.smidatesttask.controllerTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthPageController functions test
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class AuthPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * test the login page loading
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testLoadLoginPage() throws Exception {
        mockMvc.perform(get("/api/v1/auth/login_page"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Login</title>")));
    }

    /**
     * test the registration page loading
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testLoadRegistrationPage() throws Exception {
        mockMvc.perform(get("/api/v1/auth/register_page"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Registration</title>")));
    }
}

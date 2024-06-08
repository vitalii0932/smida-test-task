package org.example.smidatesttask.securityTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security test
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * test the not secured login page loading
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testGetNotSecuredLoginPage_thenAssertLoginPage() throws Exception {
        mockMvc.perform(get("/api/v1/auth/login_page"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Login</title>")));
    }

    /**
     * test the secured companies page loading
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testGetCompaniesLoginPage_thenAssertRedirectToLoginPage() throws Exception {
        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/api/v1/auth/login_page"));
    }
}

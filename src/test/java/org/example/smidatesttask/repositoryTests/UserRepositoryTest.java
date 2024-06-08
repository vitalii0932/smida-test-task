package org.example.smidatesttask.repositoryTests;

import org.example.smidatesttask.model.User;
import org.example.smidatesttask.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserRepository tests
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    /**
     * set up the test user
     */
    @Before
    public void setUp() {
        testUser = new User();
        testUser.setEmail("test_email@com.ua");
        testUser.setPassword("supersecret!");

        testUser = userRepository.save(testUser);
    }

    /**
     * tear down the test user
     */
    @After
    public void tearDown() {
        if (userRepository.findById(testUser.getId()).isPresent()) {
            userRepository.delete(testUser);
        }
    }

    /**
     * find by email test
     */
    @Test
    public void user_findUserByEmail_thenReturnUser() {
        User userByEmail = userRepository.findUserByEmail(testUser.getEmail()).orElse(null);

        assertNotNull(userByEmail);
        assertEquals(testUser, userByEmail);
    }

    /**
     * create test
     */
    @Test
    public void user_whenSaved_thenCanBeFoundById() {
        User savedUser = userRepository.findById(testUser.getId()).orElse(null);

        assertNotNull(savedUser);
        assertEquals(testUser, savedUser);
    }

    /**
     * update test
     */
    @Test
    public void user_whenUpdated_thenCanBeFoundById() {
        testUser.setEmail("new_test_email@com.ua");
        userRepository.save(testUser);

        Optional<User> updatedUser = userRepository.findById(testUser.getId());

        assertTrue(updatedUser.isPresent());
        assertEquals("new_test_email@com.ua", updatedUser.get().getEmail());
    }

    /**
     * delete test
     */
    @Test
    public void user_whenDeleted_thenCannotBeFoundById() {
        userRepository.delete(testUser);

        assertFalse(userRepository.findById(testUser.getId()).isPresent());
    }
}

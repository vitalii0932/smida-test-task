package org.example.smidatesttask.repository;

import org.example.smidatesttask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * repository interface for User entities
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * find optional user by his name
     *
     * @param email - user name
     * @return optional user
     */
    Optional<User> findUserByEmail(String email);
}

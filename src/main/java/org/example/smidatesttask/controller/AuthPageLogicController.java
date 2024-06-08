package org.example.smidatesttask.controller;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.AuthenticationRequest;
import org.example.smidatesttask.dto.AuthenticationResponse;
import org.example.smidatesttask.dto.RegisterRequest;
import org.example.smidatesttask.service.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller for auth logic
 */
@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthPageLogicController {

    private final AuthenticationService authenticationService;

    /**
     * handles the POST request for user registration
     *
     * @param request - the RegisterRequest object containing user registration data
     * @return ResponseEntity with an AuthenticationResponse containing registration status
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        try {
            return ResponseEntity.ok(authenticationService.register(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    /**
     * handles the POST request for user authentication
     *
     * @param request - the AuthenticationRequest object containing user authentication data
     * @return ResponseEntity with an AuthenticationResponse containing authentication status
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
}

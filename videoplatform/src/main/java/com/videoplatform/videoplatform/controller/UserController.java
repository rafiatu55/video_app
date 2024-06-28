package com.videoplatform.videoplatform.controller;

import com.videoplatform.videoplatform.dto.PasswordResetConfirmRequest;
import com.videoplatform.videoplatform.dto.PasswordResetRequest;
import com.videoplatform.videoplatform.entity.User;
import com.videoplatform.videoplatform.exception.UserNotFoundException;
import com.videoplatform.videoplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/user")
public class UserController {
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // Create a new user (Accessible to everyone)
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User newUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating user: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Initiate Password Reset (Accessible to everyone)
    @PostMapping("/password/reset")
    public ResponseEntity<Void> initiatePasswordReset(@RequestBody PasswordResetRequest request) {
        try {
            String email = request.getEmail();
            userService.initiatePasswordReset(email);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initiating password reset: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Reset Password (Accessible to everyone)
    @PostMapping("/password/reset/confirm")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody PasswordResetConfirmRequest request) {
        try {
            userService.resetPasswordWithToken(token, request.getNewPassword(), request.getConfirmPassword());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error resetting password: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Initiate User Verification (Accessible to everyone)
    @PostMapping("/verify")
    public ResponseEntity<Void> initiateVerification(@RequestBody String email) {
        try {
            userService.initiateVerification(email);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initiating verification: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Confirm User Verification
    @GetMapping("/verify/confirm")
    public ResponseEntity<Void> confirmVerification(@RequestParam("token") String token) {
        try {
            userService.confirmVerification(token);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error confirming verification: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

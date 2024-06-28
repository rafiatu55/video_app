package com.videoplatform.videoplatform.service;

import com.videoplatform.videoplatform.entity.Role;
import com.videoplatform.videoplatform.entity.User;
import com.videoplatform.videoplatform.exception.UserNotFoundException;
import com.videoplatform.videoplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public User createUser(User user) {
        // Set a default role if the role has not been provided
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        } else {
            if (user.getRole() != Role.USER && user.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("Invalid role. Allowed roles are User and Admin");
            }
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Generate a verification token for the user
        String verificationToken = generateVerificationToken();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpirationTime(LocalDateTime.now().plusMinutes(20)); // Valid for 20 minutes

        // Send the verification email
        String verificationLink = generateVerificationLink(verificationToken);
        emailService.SendEmail(user.getEmail(), "Account verification", "Please verify your account by clicking the verification link: " + verificationLink);

        // Save the user
        return userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("The user with email " + email + " was not found");
        }
        return user;
    }

    public void initiatePasswordReset(String email) {
        User user = findUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User with email " + email + " not found.");
        }

        // Generate a unique reset token
        String resetToken = generateResetToken();

        saveResetToken(email, resetToken);

        // Generate the reset link
        String resetLink = generateResetLink(resetToken);

        // Send the password reset email
        emailService.SendEmail(email, "Password Reset Request", "Please reset your password by clicking the link: " + resetLink);
    }

    public String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    public void saveResetToken(String email, String token) {
        User user = findUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User with email " + email + " not found.");
        }

        user.setResetToken(token);
        user.setResetTokenExpirationTime(LocalDateTime.now().plusMinutes(20)); // Valid for 20 minutes
        userRepository.save(user);
    }

    public String generateResetLink(String resetToken) {
        return "http://localhost:8080" + resetToken;
    }

    public void resetPasswordWithToken(String resetToken, String newPassword, String confirmPassword) {
        User user = verifyResetToken(resetToken);

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Clear the reset token
        user.setResetTokenExpirationTime(null); // Clear the expiration time
        userRepository.save(user);

        emailService.SendEmail(user.getEmail(), "Password Reset Successful", "Your password has been reset successfully.");
    }

    public User verifyResetToken(String resetToken) {
        User user = userRepository.findByResetToken(resetToken);
        if (user == null) {
            throw new UserNotFoundException("Invalid or expired token.");
        }

        if (user.getResetTokenExpirationTime().isBefore(LocalDateTime.now())) {
            throw new UserNotFoundException("Token has expired.");
        }
        return user;
    }

    public void initiateVerification(String email) {
        User user = findUserByEmail(email);

        String token = generateVerificationToken();
        saveVerificationToken(email, token);

        String verificationLink = generateVerificationLink(token);
        emailService.SendEmail(email, "Email Verification", "To verify your email, please click the link: " + verificationLink);
    }

    public void saveVerificationToken(String email, String token) {
        User user = findUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User with email " + email + " not found.");
        }

        user.setVerificationToken(token);
        user.setVerificationTokenExpirationTime(LocalDateTime.now().plusHours(7)); // Valid for 7 hours
        userRepository.save(user);
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    private String generateVerificationLink(String token) {
        return "http://localhost:8080" + token;
    }

    public void confirmVerification(String token) {
        User user = verifyVerificationToken(token);
        user.setVerified(true);
        userRepository.save(user);

        emailService.SendEmail(user.getEmail(), "Email Verification Successful", "Your email has been verified successfully.");
    }

    public User verifyVerificationToken(String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user == null) {
            throw new UserNotFoundException("Invalid or expired token.");
        }

        if (user.getVerificationTokenExpirationTime().isBefore(LocalDateTime.now())) {
            throw new UserNotFoundException("Token has expired.");
        }
        return user;
    }


}

package com.videoplatform.videoplatform.service;

import com.videoplatform.videoplatform.entity.Role;
import com.videoplatform.videoplatform.entity.User;
import com.videoplatform.videoplatform.exception.UserNotFoundException;
import com.videoplatform.videoplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.emailService=emailService;
    }
    public User createUser(User user) {
        //set a default role if the role has not been provided
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        } else {
            if (user.getRole() != Role.USER && user.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("Invalid role. Allowed roles are User and Admin");
            }

        }
        //Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //Generating a verification token for the user
        String verificationToken = generateVerificationToken();
        user.setVerification(verificationToken);
        user.setVerificationExpirationTime(String.valueOf(LocalDateTime.now().plusMinutes(20)));//This sets the verification tokenvalid for 20 minutes


        //Sending a verification token
        verificationToken = generateVerificationLink(verificationToken);
        emailService.SendEmail(user.getEmail(), "Account verification", "Please verify your account by clicking the verification link: " + verificationToken);

        //Saving the user
        return userRepository.save(user);
    }
    //Find a user by email
    public User findUserByEmail(String email){
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UserNotFoundException("The user with email " + email + " was not found");

        }
        return user;
    }


    //Initiate password Reset using UUID
    public void initiatePasswordReset(String email){
        User user = findUserByEmail(email);

        if (user == null){
            throw new UserNotFoundException("User with email " + email + " not found.");
        }
        //Generate a unique reset token
        String resetToken = generateResetToken();

        saveResetToken(email, resetToken);

        String resetLink = generateResetLink(resetToken);

        //Send the password reset email
        emailService.SendEmail(email, "Password Reset Request", "Please reset your password by clicking the link: " + resetLink);

    }
    public String generateResetToken(){
        //Generate the Token
        return UUID.randomUUID().toString();
    }

    public void saveResetToken(String email, String token){
        //Find the user by email
        User user = findUserByEmail(email);
        if(user == null){
            throw new UserNotFoundException("User with email " + email + " not found.");
        }

        user.setResetToken(token);
        user.setResetTokenExpirationTime(String.valueOf(LocalDateTime.now().plusMinutes(20)));// OTP is valid for 10 minutes
        userRepository.save(user);

    }
    public String generateResetLink(String resetToken){
        return "http://localhost:8080" + resetToken;
    }
    public void resetPasswordWithToken(String resetToken, String newPassword, String confirmPassword){

        //Verify the token and the associated user
        User user = verifyResetToken(resetToken);

        //Check if entered passwords match
        if(!newPassword.equals(confirmPassword)){
            throw new IllegalArgumentException("Passwords do not match.");
        }
        //Update user's password
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetToken(null); //Clear the OTP
        user.setResetTokenExpirationTime(null); //Clear the OTP expiration time
        userRepository.save(user);

        //Notification to the user for password change
        emailService.SendEmail(user.getEmail(), "Password Reset Successful", "Your password has been reset successfully.");
    }
    public User verifyResetToken(String resetToken){
        //Find the user
        User user = userRepository.findByResetToken(resetToken);

        if(user == null){
            throw new UserNotFoundException("Invalid token or Expired token");
        }

        //Check if the token has expired
        LocalDateTime currentTime = LocalDateTime.now();
        if(user.getResetTokenExpirationTime().isBefore(currentTime)){
            throw new UserNotFoundException("Token has expired");
        }
        return user;
    }
    //Initiate User Verification
    public void initiateVerification(String email){
        User user = findUserByEmail(email);

        //Generate a verification token
        String token = generateVerificationToken();

        //Save it
        saveVerificationToken(email, token);

        //Generate the verification link
        String verificationLink = generateVerificationLink(token);

        //Send the verification email
        emailService.SendEmail(email, "Email Verification", "To verify your email, please click the link: " + verificationLink);

    }
    public void saveVerificationToken(String email, String token) {
        // Find the user by email
        User user = findUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User with email " + email + " not found.");
        }
        user.setVerification(token);
        user.setVerificationExpirationTime(String.valueOf(LocalDateTime.now().plusHours(7))); // Token valid for 7 hours
        userRepository.save(user);
    }

    //Generate verification token
    private String generateVerificationToken(){
        return UUID.randomUUID().toString();
    }

    //Generate verification link
    private String generateVerificationLink(String token){
        return "http" + token;
    }

    //Verify user's email
    public void confirmVerification(String token){
        //verify token and the associated user
        User user = verifyVerificationToken(token);

        //MArk user as verified
        user.setVerified(true);
        userRepository.save(user);

        emailService.SendEmail(user.getEmail(), "Email Verification Successful", "Your email has been verified successfully");
    }
    public User verifyVerificationToken(String token){
        //Find user by verification token
        User user = userRepository.findByVerificationToken(token);

        if(user == null){
            throw new UserNotFoundException("Invalid token or expired token");
        }

        //Check if token has expired
        LocalDateTime currentTime = LocalDateTime.now();
        if (user.getVerificationExpirationTime().isBefore(currentTime)){
            throw new UserNotFoundException("Token has expired");
        }
        return user;
    }

















    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}

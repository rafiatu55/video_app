package com.videoplatform.videoplatform.repository;

import com.videoplatform.videoplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>{
    User findByEmail(String email);
    User findByResetToken(String resetToken);
    User findByVerificationToken(String verificationToken);

    Optional<User> findById(Integer id);


}

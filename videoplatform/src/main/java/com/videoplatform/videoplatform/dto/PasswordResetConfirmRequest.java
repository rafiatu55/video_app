package com.videoplatform.videoplatform.dto;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class PasswordResetConfirmRequest {

    private String newPassword;
    private String confirmPassword;

}
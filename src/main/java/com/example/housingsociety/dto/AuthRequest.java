package com.example.housingsociety.dto;

import jakarta.validation.constraints.*;

public class AuthRequest {
    @NotBlank @Email public String email;
    @NotBlank public String password;
}

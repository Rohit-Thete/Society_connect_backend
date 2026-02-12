package com.example.housingsociety.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.*;

public class UserCreateRequest {

    @NotBlank
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Name must contain only letters and spaces")
    @JsonAlias({ "fullName", "full_name" })
    public String fullName;

    @NotBlank
    @Email
    @JsonAlias({ "email" })
    public String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonAlias({ "password" })
    public String password;

    @Pattern(regexp = "^[0-9]{10}$", message = "Contact must be 10 digits")
    @JsonAlias({ "contact" })
    public String contact;

    @NotBlank
    @JsonAlias({ "role" })
    public String role;


    @JsonAlias({ "flatNumber", "flat_number","flatNo"}) // accepts both camelCase & snake_case
    public String flatNumber;
}

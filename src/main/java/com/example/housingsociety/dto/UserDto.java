package com.example.housingsociety.dto;

public record UserDto(Integer userId, String fullName, String email, String contact, String role, Boolean isApproved, String createdAt) {}

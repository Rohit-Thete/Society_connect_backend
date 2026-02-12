package com.example.housingsociety.mapper;

import com.example.housingsociety.entity.User;
import com.example.housingsociety.dto.UserDto;

import java.time.format.DateTimeFormatter;
public class UserMapper {
    public static UserDto toDto(User u){
        String created = u.getCreatedAt() == null ? null : u.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new UserDto(u.getUserId(), u.getFullName(), u.getEmail(), u.getContact(), u.getRole().name(), u.getIsApproved(), created);
    }
}

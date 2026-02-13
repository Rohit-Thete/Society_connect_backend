package com.example.housingsociety.controller;

import com.example.housingsociety.dto.ApiResponse;
import com.example.housingsociety.entity.User;
import com.example.housingsociety.service.UserApprovedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserApprovedController {

    private final UserApprovedService userApprovalService;

    public UserApprovedController(UserApprovedService userApprovalService) {
        this.userApprovalService = userApprovalService;
    }

    //  Approve user
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Integer id) {
        try {
            User approvedUser = userApprovalService.approveResident(id);
            return ResponseEntity.ok(new ApiResponse("User approved successfully", true, approvedUser));
        } catch (Exception e) {
            e.printStackTrace(); // See logs
            return ResponseEntity.status(500)
                    .body(new ApiResponse("Failed to approve user: " + e.getMessage(), false, null));
        }
    }



    @DeleteMapping("/{id}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable Integer id) {
        userApprovalService.rejectUser(id);
        return ResponseEntity.ok(new ApiResponse("User rejected and deleted", true, null));
    }
}

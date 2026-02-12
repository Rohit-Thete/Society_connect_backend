package com.example.housingsociety.controller;

import com.example.housingsociety.entity.UserDocument;
import com.example.housingsociety.service.UserDocumentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class UserDocumentController {

    private final UserDocumentService svc;
    private final Path uploadDir = Path.of("./uploads");

    public UserDocumentController(UserDocumentService svc) {
        this.svc = svc;
        try { Files.createDirectories(uploadDir); } catch (IOException ignored) {}
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> upload(@RequestParam("userId") Integer userId,
                                    @RequestParam("docType") String docType,
                                    @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Empty file");
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        UserDocument d = UserDocument.builder().user(null).docType(UserDocument.DocType.valueOf(docType.toUpperCase().replace(' ', '_')))
                .filePath(target.toString()).build();
        // Note: link to user must be set by client or updated via service to set user reference
        var saved = svc.create(d);
        return ResponseEntity.ok(saved);
    }

    @GetMapping public List<UserDocument> list(){ return svc.list(); }
}

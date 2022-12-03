package com.quiz.controllers;

import com.quiz.service.StoreFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
@CrossOrigin
public class ImageController {
    private final StoreFileService storeFileService;

    @PostMapping
    public ResponseEntity<String> saveImage(@RequestParam(value = "image") MultipartFile image) {
        String resp = "";
        if (image != null) {
            resp = storeFileService.uploadToLocalFileSystem(image);
        }
        return ResponseEntity.ok(resp);
    }
}

package com.quiz.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StoreFileService {

    @Value("${file.upload-dir}")
    private String path;

    public String uploadToLocalFileSystem(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + StringUtils.cleanPath(file.getOriginalFilename());

        File uploadDir = new File(this.path);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        Path path = Paths.get(this.path + fileName);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();
    }
}

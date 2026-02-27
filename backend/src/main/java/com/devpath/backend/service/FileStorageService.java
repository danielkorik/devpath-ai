package com.devpath.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private final String uploadDir = "uploads";

    public String saveFile(MultipartFile file) throws IOException {

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
        Files.write(filePath, file.getBytes());

        return filePath.toString();
    }
}
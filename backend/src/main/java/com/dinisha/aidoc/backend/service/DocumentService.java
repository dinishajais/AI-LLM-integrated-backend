package com.dinisha.aidoc.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dinisha.aidoc.backend.entity.Document;
import com.dinisha.aidoc.backend.enums.DocumentStatus;
import com.dinisha.aidoc.backend.repository.DocumentRepository;
import com.dinisha.aidoc.backend.response.DocumentUploadResponse;

@Service
public class DocumentService {

	@Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    public DocumentUploadResponse upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }try {
            Files.createDirectories(Paths.get(uploadDir));

            String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, storedName);

            Files.copy(file.getInputStream(), filePath);

            Document doc = new Document();
            doc.setFileName(file.getOriginalFilename());
            doc.setFilePath(filePath.toString());
            doc.setStatus(DocumentStatus.UPLOADED);
            doc.setCreatedAt(LocalDateTime.now());

            documentRepository.save(doc);

            return new DocumentUploadResponse(
                    doc.getId().toString(),
                    doc.getFileName(),
                    doc.getStatus().name()
            );

        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

    public Document getById(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }
}
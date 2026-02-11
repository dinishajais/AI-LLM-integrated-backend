package com.dinisha.aidoc.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.dinisha.aidoc.backend.entity.Document;
import com.dinisha.aidoc.backend.enums.DocumentStatus;
import com.dinisha.aidoc.backend.repository.DocumentRepository;
import com.dinisha.aidoc.backend.response.DocumentUploadResponse;

@Service
public class DocumentService {

	@Value("${file.upload-dir}")
    private String uploadDir;

    private final DocumentRepository documentRepository;
    private final RestTemplate restTemplate = new RestTemplate();


    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    public DocumentUploadResponse upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }try {
            Files.createDirectories(Paths.get(uploadDir));
            
            UUID id=UUID.randomUUID();

            String storedName = id + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, storedName);

            Files.copy(file.getInputStream(), filePath);

            Document doc = new Document();
            doc.setId(id);
            doc.setFileName(file.getOriginalFilename());
            doc.setFilePath(filePath.toString());
            doc.setStatus(DocumentStatus.UPLOADED);
            doc.setCreatedAt(LocalDateTime.now());

            documentRepository.save(doc);
            
            processWithPython(doc.getFilePath());


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
    public void processWithPython(String filePath) {

        String url = "http://localhost:8000/extract-text";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> requestBody=new HashMap<>();
        requestBody.put("file_path", filePath);
        
        ResponseEntity<String> response =
                restTemplate.postForEntity(url, requestBody, String.class);

        System.out.println("Python response: " + response.getBody());
    }
}
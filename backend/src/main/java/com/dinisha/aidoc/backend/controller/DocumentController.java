package com.dinisha.aidoc.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dinisha.aidoc.backend.response.DocumentUploadResponse;
import com.dinisha.aidoc.backend.service.DocumentService;

@RestController
@RequestMapping("/documents")
public class DocumentController {
	
    private final DocumentService documentService;
    
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<DocumentUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(documentService.upload(file));
    }
}
    



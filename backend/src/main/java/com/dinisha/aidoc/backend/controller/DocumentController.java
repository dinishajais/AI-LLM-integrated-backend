package com.dinisha.aidoc.backend.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dinisha.aidoc.backend.entity.Document;
import com.dinisha.aidoc.backend.exception.BadRequestException;
import com.dinisha.aidoc.backend.response.DocumentUploadResponse;
import com.dinisha.aidoc.backend.service.DocumentService;

@RestController
@RequestMapping("/documents")
public class DocumentController {
	
    private final DocumentService documentService;
    
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    @PostMapping("/uploads")
    public ResponseEntity<DocumentUploadResponse> upload(@RequestParam("file") MultipartFile file) {
    	if (file == null || file.isEmpty()) {
    	    throw new BadRequestException("File must not be empty");
    	}
        return ResponseEntity.ok(documentService.upload(file));
    }
    
    @GetMapping("/getFile/{id}")
    public ResponseEntity<Document> getFileById(@PathVariable UUID id){
    	return ResponseEntity.ok(documentService.getById(id));
    }
}
    



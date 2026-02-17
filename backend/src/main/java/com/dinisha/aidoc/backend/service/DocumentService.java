package com.dinisha.aidoc.backend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dinisha.aidoc.backend.entity.Document;
import com.dinisha.aidoc.backend.exception.BadRequestException;
import com.dinisha.aidoc.backend.exception.ResourceNotFoundException;
import com.dinisha.aidoc.backend.helper.DocumentHelper;
import com.dinisha.aidoc.backend.repository.DocumentRepository;
import com.dinisha.aidoc.backend.response.DocumentUploadResponse;

@Service
public class DocumentService {

	private final DocumentRepository documentRepository;
	private final DocumentHelper documentHelper;
	private final DocumentProcessingService documentProcessingService;

	public DocumentService(DocumentRepository documentRepository,DocumentHelper documentHelper,DocumentProcessingService documentProcessingService) {
		this.documentRepository = documentRepository;
		this.documentHelper = documentHelper;
		this.documentProcessingService=documentProcessingService;
	}

	public DocumentUploadResponse upload(MultipartFile file) {

		if (file == null || file.isEmpty()) {
			throw new BadRequestException("File must not be empty");
		}
		try {
			Document doc = documentHelper.CreateDirectoryAndSaveInDb(file);
			documentProcessingService.processDocument(doc);
			return new DocumentUploadResponse(doc.getId().toString(), doc.getFileName(), doc.getStatus().name());

		} catch (Exception e) {
			throw new BadRequestException("File upload failed: " + e.getMessage());
		}
	}

	public Document getById(UUID id) {
		return documentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Document not found"));
	}
}
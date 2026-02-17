package com.dinisha.aidoc.backend.helper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.dinisha.aidoc.backend.entity.Document;
import com.dinisha.aidoc.backend.entity.DocumentChunk;
import com.dinisha.aidoc.backend.enums.DocumentStatus;
import com.dinisha.aidoc.backend.exception.PythonResponseParseException;
import com.dinisha.aidoc.backend.repository.DocumentChunkRepository;
import com.dinisha.aidoc.backend.repository.DocumentRepository;
import com.dinisha.aidoc.backend.response.ExtractionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DocumentHelper {

	@Value("${file.upload-dir}")
	private String uploadDir;
	private final DocumentRepository documentRepository;
	private final DocumentChunkRepository documentChunkRepository;
	ObjectMapper mapper = new ObjectMapper();


	public DocumentHelper(DocumentRepository documentRepository, DocumentChunkRepository documentChunkRepository) {
		this.documentRepository = documentRepository;
		this.documentChunkRepository = documentChunkRepository;
	}

	public Document CreateDirectoryAndSaveInDb(MultipartFile file) {
		Document doc = new Document();

		try {
			Files.createDirectories(Paths.get(uploadDir));
			UUID id = UUID.randomUUID();
			String storedName = id + "_" + file.getOriginalFilename();
			Path filePath = Paths.get(uploadDir, storedName);

			Files.copy(file.getInputStream(), filePath);

			doc.setId(id);
			doc.setFileName(file.getOriginalFilename());
			doc.setFilePath(filePath.toString());
			doc.setStatus(DocumentStatus.UPLOADED);
			doc.setCreatedAt(LocalDateTime.now());
			documentRepository.save(doc);

		} catch (Exception e) {
			doc.setStatus(DocumentStatus.FAILED);
			throw new RuntimeException("Error occured while saving and uploading file");
		}

		return doc;
	}

	public Document updateStatusOfDocument(ExtractionResponse data, Document doc) {
		if (data == null || data.getError() != null) {
			doc.setStatus(DocumentStatus.FAILED);
			throw new RuntimeException("Error occured in Python Processing the file");
		} else {
			try {
				String json = mapper.writeValueAsString(data);
				doc.setExtractedText(json);
				doc.setStatus(DocumentStatus.PROCESSING);
				documentRepository.save(doc);
				return doc;
			} catch (JsonProcessingException e) {
				throw new PythonResponseParseException("Invalid json from Python Service", e);
			} catch (Exception e) {
				throw new RuntimeException("Error while updating status of doc to processing" + e);
			}
		}
	}

	private List<String> splitIntoChunks(String text, int chunkSize) {

		try {

			List<String> chunks = new ArrayList<>();

			for (int i = 0; i < text.length(); i += chunkSize) {
				int end = Math.min(i + chunkSize, text.length());
				chunks.add(text.substring(i, end));
			}

			return chunks;
		} catch (Exception e) {
			throw new RuntimeException("Error while splitting chunks");
		}
	}

	public void storeChunksInDb(Document doc) {
		try {
			String preview=mapper.readValue(doc.getExtractedText(),ExtractionResponse.class).getPreview();
	        List<String> chunks = splitIntoChunks(preview, 1000);
	        int index = 0;
	        for (String chunkText : chunks) {
	            DocumentChunk chunk = new DocumentChunk();
	            chunk.setDocumentId(doc.getId());
	            chunk.setContent(chunkText);
	            chunk.setChunkIndex(index++);
	            documentChunkRepository.save(chunk);
	        }
	         
		}
		catch(Exception e) {
			throw new RuntimeException("Exception while saving chunk in chunk repo");
		}

    }
}

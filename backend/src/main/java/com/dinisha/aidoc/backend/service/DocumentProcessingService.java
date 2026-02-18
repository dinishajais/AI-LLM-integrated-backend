package com.dinisha.aidoc.backend.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.dinisha.aidoc.backend.entity.Document;
import com.dinisha.aidoc.backend.enums.DocumentStatus;
import com.dinisha.aidoc.backend.helper.DocumentHelper;
import com.dinisha.aidoc.backend.repository.DocumentRepository;
import com.dinisha.aidoc.backend.response.ExtractionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DocumentProcessingService {
	
	private final PythonClient pythonClient;
	private final DocumentHelper documentHelper;
	private final DocumentRepository documentRepository;
	
	private static final Logger log =
	        LoggerFactory.getLogger(DocumentProcessingService.class);



	public DocumentProcessingService(PythonClient pythonClient,DocumentHelper documentHelper,DocumentRepository documentRepository) {
		this.pythonClient = pythonClient;
		this.documentHelper = documentHelper;
		this.documentRepository=documentRepository;
	}
	@Async
	public void processDocument(Document doc){
		try {
			ExtractionResponse data = pythonClient.processWithPython(doc.getFilePath());
			doc = documentHelper.updateStatusOfDocument(data, doc);
			documentHelper.storeVectorChunksInDb(doc);
			doc.setStatus(DocumentStatus.READY);
			documentRepository.save(doc);
		} catch (Exception  e) {
			doc.setStatus(DocumentStatus.FAILED);
			log.error("Error while processing document"+e);
			e.printStackTrace();
			documentRepository.save(doc);
		}
		 
	}

}

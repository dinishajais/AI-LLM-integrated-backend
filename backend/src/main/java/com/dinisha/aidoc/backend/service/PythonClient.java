package com.dinisha.aidoc.backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.dinisha.aidoc.backend.exception.PythonServiceException;
import com.dinisha.aidoc.backend.response.EmbeddingResponse;
import com.dinisha.aidoc.backend.response.ExtractionResponse;

@Service
public class PythonClient {

	private final RestTemplate restTemplate = new RestTemplate();
	
	@Value("${python.service.baseUrl}")
	private String baseUrl;

	public ExtractionResponse processWithPython(String filePath) {
    	
    	try {

        String url = baseUrl+"/extract-text";
        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> requestBody=new HashMap<>();
        requestBody.put("file_path", filePath);
        
        ResponseEntity<ExtractionResponse> response =
                restTemplate.postForEntity(url, requestBody, ExtractionResponse.class);
        
        return response.getBody();
    	}
    	catch(ResourceAccessException ex) {
    		throw new PythonServiceException("Python service is unavailable", ex);
    	}
    	catch (HttpClientErrorException ex) {
    		throw new PythonServiceException(
    		        "Python service returned error: " + ex.getResponseBodyAsString(),
    		        ex);    	
    	}
    	catch(Exception e) {
    		throw new RuntimeException("Error occurred while processing file with Python");
    	}

    }
	public EmbeddingResponse getEmbedding(String text) {

	    try {
	        String url = baseUrl + "/embed";

	        Map<String, String> requestBody = Map.of("text", text);

	        ResponseEntity<EmbeddingResponse> response =
	                restTemplate.postForEntity(
	                        url,
	                        requestBody,
	                        EmbeddingResponse.class
	                );

	        return response.getBody();

	    } catch (ResourceAccessException ex) {
	        throw new PythonServiceException(
	                "Python service is unavailable", ex);
	    } catch (HttpClientErrorException ex) {
	        throw new PythonServiceException(
	                "Python service returned error: "
	                        + ex.getResponseBodyAsString(),
	                ex);
	    }
	}

}

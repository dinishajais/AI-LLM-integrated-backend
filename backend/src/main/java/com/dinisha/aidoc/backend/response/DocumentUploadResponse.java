package com.dinisha.aidoc.backend.response;
public record DocumentUploadResponse(
        String documentId,
        String fileName,
        String status
) {}
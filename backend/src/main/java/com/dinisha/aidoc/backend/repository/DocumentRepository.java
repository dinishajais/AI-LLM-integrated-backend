package com.dinisha.aidoc.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dinisha.aidoc.backend.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
}

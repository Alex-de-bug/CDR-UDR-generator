package com.alwx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alwx.model.CdrRecord;

public interface CdrRecordRepository extends JpaRepository<CdrRecord, Long>{
    
}

package com.alwx.call.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alwx.call.model.Call;

public interface CallRepository extends JpaRepository<Call, Long>{
    
}

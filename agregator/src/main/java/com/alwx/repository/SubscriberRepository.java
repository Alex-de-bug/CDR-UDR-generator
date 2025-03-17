package com.alwx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alwx.model.Subscriber;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long>{
}

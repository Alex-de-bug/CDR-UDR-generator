package com.alwx.subscriber.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alwx.subscriber.model.Subscriber;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long>{
}

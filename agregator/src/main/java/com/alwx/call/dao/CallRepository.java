package com.alwx.call.dao;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alwx.call.model.Call;

public interface CallRepository extends JpaRepository<Call, Long>{
    @Query("SELECT c FROM Call c " +
           "WHERE MONTH(c.startTime) = :month " +
           "AND ((:sub = c.caller.id AND c.callType = '02') " +
           "OR (:sub = c.receiver.id AND c.callType = '01'))")
    List<Call> findCallsByMonthAndSub(@Param("month") int month, 
                                        @Param("sub") Long sub);

    @Query("SELECT c FROM Call c " +
           "WHERE ((:sub = c.caller.id AND c.callType = '02') " +
           "OR (:sub = c.receiver.id AND c.callType = '01'))")
    List<Call> findCallsBySub(@Param("sub") Long sub);

    @Query("SELECT c FROM Call c " +
           "WHERE MONTH(c.startTime) = :month")
    List<Call> findCallsByMonth(@Param("month") int month);

    @Query("SELECT c FROM Call c " +
           "WHERE c.startTime >= :startDate AND c.endTime <= :endDate " +
           "AND ((:sub = c.caller.id AND c.callType = '02') " +
           "OR (:sub = c.receiver.id AND c.callType = '01'))")
    List<Call> findCallsBySubscriberAndPeriod(
        @Param("sub") Long subscriberId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
}

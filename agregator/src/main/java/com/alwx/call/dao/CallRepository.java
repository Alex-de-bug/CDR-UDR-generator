package com.alwx.call.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alwx.call.model.Call;

public interface CallRepository extends JpaRepository<Call, Long>{
    @Query("SELECT c FROM Call c " +
           "WHERE MONTH(c.startTime) = :month " +
           "AND ((:phoneNumber = c.caller.id AND c.callType = '02') " +
           "OR (:phoneNumber = c.receiver.id AND c.callType = '01'))")
    List<Call> findCallsByMonthAndPhone(@Param("month") int month, 
                                      @Param("phoneNumber") Long phoneNumber);

    @Query("SELECT c FROM Call c " +
        "WHERE ((:phoneNumber = c.caller.id AND c.callType = '02') " +
        "OR (:phoneNumber = c.receiver.id AND c.callType = '01'))")
    List<Call> findCallsByPhone(@Param("phoneNumber") Long phoneNumber);

    @Query("SELECT c FROM Call c " +
           "WHERE MONTH(c.startTime) = :month ")
    List<Call> findCallsByMonth(@Param("month") int month);
}

package com.alwx.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "cdr_record")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class CdrRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    
    @Column(name = "call_type")
    @Enumerated(EnumType.STRING)
    private CallType callType;

    @Column(name = "caller_number")
    private String callerNumber;

    @Column(name = "receiver_number")
    private String receiverNumber;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;
}

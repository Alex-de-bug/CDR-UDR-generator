package com.alwx.call.model;

import java.time.LocalDateTime;

import com.alwx.call.utils.CallTypeConverter;
import com.alwx.subscriber.model.Subscriber;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "call")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "call_type")
    @Convert(converter = CallTypeConverter.class)
    private CallType callType;

    @ManyToOne
    @JoinColumn(name = "caller", nullable = false)
    private Subscriber caller;

    @ManyToOne
    @JoinColumn(name = "receiver", nullable = false)
    private Subscriber receiver;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;
}

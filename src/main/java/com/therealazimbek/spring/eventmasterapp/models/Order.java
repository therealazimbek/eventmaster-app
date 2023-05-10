package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order {

    public Order(BigDecimal total, LocalDateTime date, User user, Event event) {
        this.total = total;
        this.date = date;
        this.user = user;
        this.event = event;
    }

    @Id
    @GeneratedValue
    private long id;

    private BigDecimal total;

    private LocalDateTime date;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private Event event;
}

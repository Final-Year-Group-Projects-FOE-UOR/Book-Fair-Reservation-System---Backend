package com.bookfair.bookfairreservationsystembackend.models.reservation;


import com.bookfair.bookfairreservationsystembackend.models.stall.Stall;
import com.bookfair.bookfairreservationsystembackend.models.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "stall_id", nullable = false)
    private Stall stall;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private boolean checkedIn;

}

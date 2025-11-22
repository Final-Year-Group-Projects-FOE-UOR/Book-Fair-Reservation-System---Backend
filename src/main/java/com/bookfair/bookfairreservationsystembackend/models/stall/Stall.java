package com.bookfair.bookfairreservationsystembackend.models.stall;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="stalls")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Stall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String stallName;

    @Enumerated(EnumType.STRING)
    private StallType type;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String dimensions;

    @Embedded
    private MapMetadata mapMetadata;

    @Column(nullable = false)
    private boolean available=true;
}

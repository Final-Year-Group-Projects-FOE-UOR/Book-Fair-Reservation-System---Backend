package com.bookfair.bookfairreservationsystembackend.models.vendor;

import com.bookfair.bookfairreservationsystembackend.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "business_name")
    private String businessName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vendor_genres", joinColumns = @JoinColumn(name = "vendor_id"))
    @Column(name = "genre")
    private List<String> genres;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
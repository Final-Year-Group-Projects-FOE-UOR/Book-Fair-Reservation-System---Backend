package com.bookfair.bookfairreservationsystembackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.bookfair.bookfairreservationsystembackend")
@EnableJpaRepositories(basePackages = "com.bookfair.bookfairreservationsystembackend.repositories")
@EntityScan(basePackages = "com.bookfair.bookfairreservationsystembackend.models")
public class BookFairReservationSystemBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookFairReservationSystemBackendApplication.class, args);
    }
}

package com.bookfair.bookfairreservationsystembackend.repositories;

import com.bookfair.bookfairreservationsystembackend.models.stall.BookfairMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookfairMapRepository extends JpaRepository<BookfairMap, Integer> {
}

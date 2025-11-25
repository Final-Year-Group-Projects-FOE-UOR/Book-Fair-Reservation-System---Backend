package com.bookfair.bookfairreservationsystembackend.services.admin;

import com.bookfair.bookfairreservationsystembackend.dtos.request.MapRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.MapResponse;
import com.bookfair.bookfairreservationsystembackend.models.stall.BookfairMap;
import com.bookfair.bookfairreservationsystembackend.repositories.BookfairMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMapService {

    private final BookfairMapRepository mapRepository;

    public MapResponse addOrUpdateMap(MapRequest request) {
        List<BookfairMap> maps = mapRepository.findAll();
        BookfairMap map = maps.isEmpty() ? new BookfairMap() : maps.get(0);

        map.setMapUrl(request.getMapUrl());
        map.setDescription(request.getDescription());
        map.setActive(true);

        BookfairMap savedMap = mapRepository.save(map);
        return mapToResponse(savedMap);
    }

    public MapResponse getMap() {
        BookfairMap map = mapRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Map not set yet"));
        return mapToResponse(map);
    }


    private MapResponse mapToResponse(BookfairMap map) {
        return new MapResponse(
                map.getId(),
                map.getMapUrl(),
                map.getDescription(),
                map.getCreatedAt(),
                map.isActive()
        );
    }
}

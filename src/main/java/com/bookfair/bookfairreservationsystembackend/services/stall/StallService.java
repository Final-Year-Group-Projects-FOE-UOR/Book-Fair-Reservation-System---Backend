package com.bookfair.bookfairreservationsystembackend.services.stall;

import com.bookfair.bookfairreservationsystembackend.dtos.request.StallRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.StallResponse;
import com.bookfair.bookfairreservationsystembackend.models.Stall;
import com.bookfair.bookfairreservationsystembackend.repositories.StallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor

public class StallService {


    private final StallRepository stallRepository;

    public StallResponse addStall(StallRequest request) {
        stallRepository.findByStallName(request.getStallName())
                .ifPresent(s -> {
                    throw new IllegalArgumentException("Stall with name " + request.getStallName() + " already exists.");
                });
        Stall stall = new Stall();
        stall.setStallName(request.getStallName());
        stall.setType(request.getType());
        stall.setPrice(request.getPrice());
        stall.setDimensions(request.getDimensions());
        stall.setLocationCode(request.getLocationCode());
        stall.setAvailable(true);

        Stall saved= stallRepository.save(stall);
        return mapToResponse(saved);

    }
    private StallResponse mapToResponse(Stall stall) {
        return new StallResponse(
                stall.getId(),
                stall.getStallName(),
                stall.getType(),
                stall.getPrice(),
                stall.getDimensions(),
                stall.getLocationCode(),
                stall.isAvailable()
        );
    }

    public StallResponse updateStall(Integer id, StallRequest request) {
        Stall stall = stallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stall not found with id: " + id));

        stall.setStallName(request.getStallName());
        stall.setType(request.getType());
        stall.setPrice(request.getPrice());
        stall.setDimensions(request.getDimensions());
        stall.setLocationCode(request.getLocationCode());

        Stall updated = stallRepository.save(stall);
        return mapToResponse(updated);
    }
    public void deleteStall(Integer id) {
        Stall stall = stallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stall not found with id: " + id));
        stallRepository.delete(stall);
    }
    public List<StallResponse> getAllStalls() {
        return stallRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<StallResponse> getStallsByAvailability(boolean available) {
        return stallRepository.findAll()
                .stream()
                .filter(stall -> stall.isAvailable() == available)
                .map(this::mapToResponse)
                .toList();
    }
}

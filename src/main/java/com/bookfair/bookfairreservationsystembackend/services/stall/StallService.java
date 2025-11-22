package com.bookfair.bookfairreservationsystembackend.services.stall;
import com.bookfair.bookfairreservationsystembackend.exception.BadRequestException;

import com.bookfair.bookfairreservationsystembackend.dtos.request.StallRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.StallResponse;
import com.bookfair.bookfairreservationsystembackend.exception.NotFoundException;
import com.bookfair.bookfairreservationsystembackend.models.stall.Stall;
import com.bookfair.bookfairreservationsystembackend.repositories.StallRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor

public class StallService {


    private final StallRepository stallRepository;

    public StallResponse addStall(StallRequest request) {
        stallRepository.findByStallName(request.stallName())
                .ifPresent(s -> {
                    throw new BadRequestException("Stall with name " + request.stallName() + " already exists.");
                });
        Stall stall = new Stall();
        stall.setStallName(request.stallName());
        stall.setType(request.type());
        stall.setPrice(request.price());
        stall.setDimensions(request.dimensions());
        stall.setMapMetadata(request.mapMetadata());
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
                stall.getMapMetadata(),
                stall.isAvailable()
        );
    }

    public StallResponse updateStall(Integer id, StallRequest request) {
        Stall stall = stallRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Stall not found with id: " + id));

        stall.setStallName(request.stallName());
        stall.setType(request.type());
        stall.setPrice(request.price());
        stall.setDimensions(request.dimensions());
        stall.setMapMetadata(request.mapMetadata());

        Stall updated = stallRepository.save(stall);
        return mapToResponse(updated);
    }
    public void deleteStall(Integer id) {
        Stall stall = stallRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Stall not found with id: " + id));
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

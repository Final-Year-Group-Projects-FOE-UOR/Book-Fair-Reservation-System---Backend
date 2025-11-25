package com.bookfair.bookfairreservationsystembackend.services.stall;
import com.bookfair.bookfairreservationsystembackend.exception.BadRequestException;

import com.bookfair.bookfairreservationsystembackend.dtos.request.StallRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.StallResponse;
import com.bookfair.bookfairreservationsystembackend.exception.NotFoundException;
import com.bookfair.bookfairreservationsystembackend.models.stall.MapMetadata;
import com.bookfair.bookfairreservationsystembackend.models.stall.Stall;
import com.bookfair.bookfairreservationsystembackend.models.stall.StallType;
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
        MapMetadata mapMetadata = request.mapMetadata();
        mapMetadata.setConfigured(false);

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
        stallRepository.findByStallName(request.stallName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BadRequestException(
                                "Stall name '" + request.stallName() + "' is already in use."
                        );
                    }
                });
        stall.setStallName(request.stallName());
        stall.setType(request.type());
        stall.setPrice(request.price());
        stall.setDimensions(request.dimensions());
        MapMetadata incoming = request.mapMetadata();
        MapMetadata meta = stall.getMapMetadata();

        if (meta == null) {
            meta = new MapMetadata();
        }

        meta.setMapWidth(incoming.getMapWidth());
        meta.setMapHeight(incoming.getMapHeight());
        meta.setMapWidthPercent(incoming.getMapWidthPercent());
        meta.setMapHeightPercent(incoming.getMapHeightPercent());
        meta.setMapRotation(incoming.getMapRotation());
        meta.setMapShape(incoming.getMapShape());
        meta.setMapSize(incoming.getMapSize());
        meta.setMapPosition(incoming.getMapPosition());
        meta.setConfigured(incoming.getConfigured());
        stall.setMapMetadata(meta);
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
    public StallResponse updateAvailability(Integer id, boolean available) {
        Stall stall = stallRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Stall not found with id: " + id));
        stall.setAvailable(available);
        Stall updated = stallRepository.save(stall);
        return mapToResponse(updated);
    }

    public List<StallResponse> getStallsByType(String typeStr) {
        StallType stallType;
        try {
            stallType = StallType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid StallType: " + typeStr);
        }

        return stallRepository.findAll()
                .stream()
                .filter(stall -> stall.getType() == stallType)
                .map(this::mapToResponse)
                .toList();
    }



}

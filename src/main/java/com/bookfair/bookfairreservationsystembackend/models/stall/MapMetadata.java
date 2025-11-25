package com.bookfair.bookfairreservationsystembackend.models.stall;


import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MapMetadata {
    private Double mapWidth;
    private Double mapHeight;
    private Double mapWidthPercent;
    private Double mapHeightPercent;
    private Double mapRotation;
    private String mapShape;
    private Double mapSize;
    private Boolean configured = false;
    @Embedded
    private Location mapPosition;
}

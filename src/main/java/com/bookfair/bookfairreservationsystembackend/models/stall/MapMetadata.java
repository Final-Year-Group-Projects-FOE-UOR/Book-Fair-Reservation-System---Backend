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
    private double mapWidth;
    private double mapHeight;
    private double mapWidthPercent;
    private double mapHeightPercent;
    private double mapRotation;
    private String mapShape;
    private double mapSize;

    @Embedded
    private Location mapPosition;
}

package com.bubblepat.backend.dto;

import lombok.Data;

@Data
public class BreedInfoDTO {
    private String name;
    private String imageLink;
    private Integer minLifeExpectancy;
    private Integer maxLifeExpectancy;
    private Double minHeightMale;
    private Double maxHeightMale;
    private Double minHeightFemale;
    private Double maxHeightFemale;
    private Double minWeightMale;
    private Double maxWeightMale;
    private Double minWeightFemale;
    private Double maxWeightFemale;
    private Integer energy;
    private Integer trainability;
    private Integer goodWithChildren;
    private Integer goodWithOtherDogs;
    private Integer goodWithStrangers;
    private Integer shedding;
    private Integer barking;
    private Integer protectiveness;
    private Integer playfulness;
    private Integer grooming;
    private Integer drooling;
    private Integer coatLength;
}

package com.bubblepat.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatBreedDTO {
    private String id;
    private String name;
    private String temperament;
    private String origin;
    private String lifeSpan;
    private String description;
    private Weight weight;
    private String referenceImageId;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weight {
        private String imperial;
        private String metric;
    }
}

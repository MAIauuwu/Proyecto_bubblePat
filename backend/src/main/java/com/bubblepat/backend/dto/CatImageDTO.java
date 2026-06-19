package com.bubblepat.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatImageDTO {
    private String id;
    private String url;
    private List<CatBreedDTO> breeds;
}

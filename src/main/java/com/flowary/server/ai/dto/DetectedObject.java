package com.flowary.server.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DetectedObject(
        @JsonProperty("name_ko") String nameKo,
        @JsonProperty("name_en") String nameEn,
        String meaning,
        List<Integer> box2d
) {}
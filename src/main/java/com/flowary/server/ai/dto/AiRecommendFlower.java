package com.flowary.server.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiRecommendFlower(
        @JsonProperty("name_ko") String nameKo,
        @JsonProperty("name_en") String nameEn,
        String meaning
) {}
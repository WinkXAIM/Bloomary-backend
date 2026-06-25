package com.flowary.server.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiRecommendResponse(
        String title,
        @JsonProperty("combined_message") String content,
        List<AiRecommendFlower> flowers

) {}
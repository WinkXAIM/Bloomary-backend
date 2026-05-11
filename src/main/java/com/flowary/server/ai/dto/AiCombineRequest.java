package com.flowary.server.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiCombineRequest(
        @JsonProperty("flower_names") List<String> flowerNames
) {}
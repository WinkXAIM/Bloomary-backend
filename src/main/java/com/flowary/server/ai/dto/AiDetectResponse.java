package com.flowary.server.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiDetectResponse(
        @JsonProperty("detected_objects") List<DetectedObject> detectedObjects
) {}
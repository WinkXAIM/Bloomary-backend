package com.flowary.server.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiCombineResponse(
        @JsonProperty("combined_message_ko") String combinedMessageKo,
        @JsonProperty("story_preview_en") String storyPreviewEn,
        @JsonProperty("summary_en") String summaryEn
) {}
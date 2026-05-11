package com.flowary.server.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IndividualMeaning(
        @JsonProperty("name_ko") String nameKo,
        String meaning
) {}
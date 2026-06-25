package com.flowary.server.analysis.dto;

import java.time.LocalDateTime;

public record AnalysisListItem(
        String id,
        String summary,
        String imgUrl,
        LocalDateTime createdAt
) {}
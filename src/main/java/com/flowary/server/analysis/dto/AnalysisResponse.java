package com.flowary.server.analysis.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AnalysisResponse(
        String id,
        String summary,
        String content,
        String story,
        List<AnalysisFlowerItem> flowerMeanings,
        LocalDateTime createdAt
) {}
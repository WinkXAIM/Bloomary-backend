package com.flowary.server.ai.dto;

import java.util.List;

public record AiRecommendResponse(
        String title,
        String content,
        List<AiRecommendFlower> flowers
) {}
package com.flowary.server.recommendation.dto;

import java.util.List;

public record RecommendResponse(String title, String content, List<RecommendFlowerItem> flowers) {}
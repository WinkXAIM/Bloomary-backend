package com.flowary.server.analysis.dto;

import java.util.List;

public record AnalysisCreateRequest(List<AnalysisFlowerRequest> flowers) {}
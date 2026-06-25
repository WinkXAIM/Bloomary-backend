package com.flowary.server.analysis.dto;

import java.util.List;

public record AnalysisPageResponse(
        List<AnalysisResponse> analyses,
        boolean hasNextPage
) {}
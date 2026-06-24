package com.flowary.server.analysis.dto;

import java.util.List;

public record AnalysisFlowerRequest(String nameKo, String nameEn, String meaning, List<Integer> box2d) {}
package com.flowary.server.flower.dto;

import java.util.List;

public record FlowerItem(String nameKo, String nameEn, String meaning, List<Integer> box2d) {}
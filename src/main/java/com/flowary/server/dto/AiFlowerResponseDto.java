package com.flowary.server.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiFlowerResponseDto {
    private List<DetectedObject> detected_objects;

    @Data
    public static class DetectedObject {
        private String name_ko;
        private String name_en;
        private String meaning;
        private List<Integer> box2d;
    }
}
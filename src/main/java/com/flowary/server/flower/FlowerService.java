package com.flowary.server.flower;

import com.flowary.server.ai.AiClient;
import com.flowary.server.flower.dto.FlowerItem;
import com.flowary.server.flower.dto.FlowerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlowerService {

    private final AiClient aiClient;

    public FlowerResponse detectFlowers(MultipartFile image) throws IOException {
        List<FlowerItem> flowers = aiClient.detectFlowers(image).detectedObjects().stream()
                .map(obj -> new FlowerItem(obj.nameKo(), obj.box2d()))
                .toList();
        return new FlowerResponse(flowers);
    }
}
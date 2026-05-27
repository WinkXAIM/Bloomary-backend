package com.flowary.server.recommendation;

import com.flowary.server.ai.AiClient;
import com.flowary.server.ai.dto.AiRecommendResponse;
import com.flowary.server.recommendation.dto.RecommendFlowerItem;
import com.flowary.server.recommendation.dto.RecommendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final AiClient aiClient;

    public RecommendResponse recommend(String situation) {
        AiRecommendResponse aiResponse = aiClient.recommend(situation);

        List<RecommendFlowerItem> flowers = aiResponse.flowers().stream()
                .map(f -> new RecommendFlowerItem(f.nameKo(), f.nameEn(), f.meaning()))
                .toList();

        return new RecommendResponse(aiResponse.title(), aiResponse.content(), flowers);
    }
}
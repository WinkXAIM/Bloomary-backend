package com.flowary.server.recommendation;

import com.flowary.server.recommendation.dto.RecommendRequest;
import com.flowary.server.recommendation.dto.RecommendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendResponse recommend(@RequestBody RecommendRequest request) {
        return recommendationService.recommend(request.user_situation());
    }
}
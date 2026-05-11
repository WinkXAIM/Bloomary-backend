package com.flowary.server.analysis;

import com.flowary.server.ai.AiClient;
import com.flowary.server.ai.dto.AiCombineResponse;
import com.flowary.server.analysis.dto.AnalysisFlowerItem;
import com.flowary.server.analysis.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AiClient aiClient;
    private final AnalysisRepository analysisRepository;

    public AnalysisResponse createAnalysis(MultipartFile image, List<String> flowerNames) {
        AiCombineResponse aiResponse = aiClient.combineFloriography(flowerNames);

        List<AnalysisFlower> flowers = aiResponse.individualMeanings().stream()
                .map(m -> AnalysisFlower.builder()
                        .name(m.nameKo())
                        .meaning(m.meaning())
                        .build())
                .toList();

        Analysis analysis = Analysis.builder()
                .summary(aiResponse.summaryEn())
                .content(aiResponse.combinedMessageKo())
                .story(aiResponse.storyPreviewEn())
                .flowers(flowers)
                .build();

        Analysis saved = analysisRepository.save(analysis);

        List<AnalysisFlowerItem> flowerItems = saved.getFlowers().stream()
                .map(f -> new AnalysisFlowerItem(f.getName(), f.getMeaning()))
                .toList();

        return new AnalysisResponse(
                String.valueOf(saved.getId()),
                saved.getSummary(),
                saved.getContent(),
                saved.getStory(),
                flowerItems,
                saved.getCreatedAt()
        );
    }
}
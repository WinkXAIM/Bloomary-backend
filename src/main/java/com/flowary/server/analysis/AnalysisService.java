package com.flowary.server.analysis;

import com.flowary.server.ai.AiClient;
import com.flowary.server.ai.dto.AiCombineResponse;
import com.flowary.server.ai.dto.FlowerInput;
import com.flowary.server.analysis.dto.AnalysisFlowerItem;
import com.flowary.server.analysis.dto.AnalysisFlowerRequest;
import com.flowary.server.analysis.dto.AnalysisResponse;
import com.flowary.server.flower.FlowerImageStore;
import com.flowary.server.flower.TempFileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AiClient aiClient;
    private final AnalysisRepository analysisRepository;
    private final TempFileStore tempFileStore;
    private final FlowerImageStore flowerImageStore;

    public AnalysisResponse getAnalysis(Long id) {
        Analysis analysis = analysisRepository.findByIdWithFlowers(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<AnalysisFlowerItem> flowerItems = analysis.getFlowers().stream()
                .map(f -> new AnalysisFlowerItem(f.getNameKo(), f.getNameEn(), f.getMeaning()))
                .toList();

        return new AnalysisResponse(
                String.valueOf(analysis.getId()),
                analysis.getSummary(),
                analysis.getContent(),
                analysis.getStory(),
                flowerItems,
                analysis.getImgUrl(),
                analysis.getCreatedAt()
        );
    }

    public List<AnalysisResponse> getAnalyses(Long userId) {
        return analysisRepository.findByUserId(userId).stream()
                .map(analysis -> {
                    List<AnalysisFlowerItem> flowerItems = analysis.getFlowers().stream()
                            .map(f -> new AnalysisFlowerItem(f.getNameKo(), f.getNameEn(), f.getMeaning()))
                            .toList();
                    return new AnalysisResponse(
                            String.valueOf(analysis.getId()),
                            analysis.getSummary(),
                            analysis.getContent(),
                            analysis.getStory(),
                            flowerItems,
                            analysis.getImgUrl(),
                            analysis.getCreatedAt()
                    );
                })
                .toList();
    }

    public AnalysisResponse createAnalysis(Long userId, List<AnalysisFlowerRequest> flowerRequests) throws IOException {
        List<FlowerInput> flowerInputs = flowerRequests.stream()
                .map(f -> new FlowerInput(f.nameKo(), f.nameEn(), f.meaning()))
                .toList();

        AiCombineResponse aiResponse = aiClient.combineFloriography(flowerInputs);

        String imgUrl = null;
        Optional<Path> tempFile = tempFileStore.findByUserId(userId);
        if (tempFile.isPresent()) {
            imgUrl = flowerImageStore.moveFromTemp(tempFile.get());
        } else {
            log.warn("temp 이미지 없음 userId={}", userId);
        }

        List<AnalysisFlower> flowers = flowerRequests.stream()
                .map(f -> AnalysisFlower.builder()
                        .nameKo(f.nameKo())
                        .nameEn(f.nameEn())
                        .meaning(f.meaning())
                        .box2d(f.box2d() != null ? f.box2d().stream().map(String::valueOf).collect(Collectors.joining(",")) : null)
                        .build())
                .toList();

        Analysis analysis = Analysis.builder()
                .summary(aiResponse.summaryEn())
                .content(aiResponse.combinedMessageKo())
                .story(aiResponse.storyPreviewEn())
                .userId(userId)
                .imgUrl(imgUrl)
                .flowers(flowers)
                .build();

        Analysis saved = analysisRepository.save(analysis);

        List<AnalysisFlowerItem> flowerItems = saved.getFlowers().stream()
                .map(f -> new AnalysisFlowerItem(f.getNameKo(), f.getNameEn(), f.getMeaning()))
                .toList();

        return new AnalysisResponse(
                String.valueOf(saved.getId()),
                saved.getSummary(),
                saved.getContent(),
                saved.getStory(),
                flowerItems,
                saved.getImgUrl(),
                saved.getCreatedAt()
        );
    }
}
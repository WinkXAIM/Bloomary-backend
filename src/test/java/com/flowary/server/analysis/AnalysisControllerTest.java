package com.flowary.server.analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowary.server.ai.AiClient;
import com.flowary.server.ai.dto.AiCombineResponse;
import com.flowary.server.ai.dto.FlowerInput;
import com.flowary.server.analysis.dto.AnalysisFlowerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AnalysisControllerTest {

    @Mock
    private AiClient aiClient;

    @Mock
    private AnalysisRepository analysisRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AnalysisService analysisService = new AnalysisService(aiClient, analysisRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(new AnalysisController(analysisService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createAnalysis_returns_201_with_analysis_data() throws Exception {
        List<FlowerInput> expectedInputs = List.of(
                new FlowerInput("장미", "Rose", "사랑"),
                new FlowerInput("튤립", "Tulip", "희망")
        );

        given(aiClient.combineFloriography(eq(expectedInputs)))
                .willReturn(new AiCombineResponse(
                        "당신의 사랑은 영원합니다",
                        "Your love blooms eternally",
                        "A bouquet of love and hope"
                ));

        Analysis savedAnalysis = Analysis.builder()
                .id(1L)
                .summary("A bouquet of love and hope")
                .content("당신의 사랑은 영원합니다")
                .story("Your love blooms eternally")
                .flowers(List.of(
                        AnalysisFlower.builder().id(1L).nameKo("장미").nameEn("Rose").meaning("사랑").build(),
                        AnalysisFlower.builder().id(2L).nameKo("튤립").nameEn("Tulip").meaning("희망").build()
                ))
                .createdAt(LocalDateTime.of(2026, 5, 11, 12, 0, 0))
                .build();

        given(analysisRepository.save(any())).willReturn(savedAnalysis);

        List<AnalysisFlowerRequest> flowerRequests = List.of(
                new AnalysisFlowerRequest("장미", "Rose", "사랑"),
                new AnalysisFlowerRequest("튤립", "Tulip", "희망")
        );

        MockMultipartFile flowers = new MockMultipartFile(
                "flowers", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(flowerRequests)
        );

        mockMvc.perform(multipart("/analyses").file(flowers))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.summary").value("A bouquet of love and hope"))
                .andExpect(jsonPath("$.content").value("당신의 사랑은 영원합니다"))
                .andExpect(jsonPath("$.story").value("Your love blooms eternally"))
                .andExpect(jsonPath("$.flowerMeanings").isArray())
                .andExpect(jsonPath("$.flowerMeanings.length()").value(2))
                .andExpect(jsonPath("$.flowerMeanings[0].nameKo").value("장미"))
                .andExpect(jsonPath("$.flowerMeanings[0].nameEn").value("Rose"))
                .andExpect(jsonPath("$.flowerMeanings[0].meaning").value("사랑"))
                .andExpect(jsonPath("$.flowerMeanings[1].nameKo").value("튤립"))
                .andExpect(jsonPath("$.flowerMeanings[1].nameEn").value("Tulip"))
                .andExpect(jsonPath("$.flowerMeanings[1].meaning").value("희망"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createAnalysis_returns_empty_flowerMeanings_when_no_flowers_given() throws Exception {
        given(aiClient.combineFloriography(any()))
                .willReturn(new AiCombineResponse(
                        "조화로운 꽃다발",
                        "A harmonious bouquet",
                        "Harmony"
                ));

        Analysis savedAnalysis = Analysis.builder()
                .id(2L)
                .summary("Harmony")
                .content("조화로운 꽃다발")
                .story("A harmonious bouquet")
                .flowers(List.of())
                .createdAt(LocalDateTime.now())
                .build();

        given(analysisRepository.save(any())).willReturn(savedAnalysis);

        MockMultipartFile flowers = new MockMultipartFile(
                "flowers", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(List.of())
        );

        mockMvc.perform(multipart("/analyses").file(flowers))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.flowerMeanings").isArray())
                .andExpect(jsonPath("$.flowerMeanings.length()").value(0));
    }
}
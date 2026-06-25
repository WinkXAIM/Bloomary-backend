package com.flowary.server.analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowary.server.ai.AiClient;
import com.flowary.server.ai.dto.AiCombineResponse;
import com.flowary.server.ai.dto.FlowerInput;
import com.flowary.server.analysis.dto.AnalysisFlowerRequest;
import com.flowary.server.auth.JwtAuthFilter;
import com.flowary.server.flower.FlowerImageStore;
import com.flowary.server.flower.TempFileStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AnalysisControllerTest {

    @Mock
    private AiClient aiClient;

    @Mock
    private AnalysisRepository analysisRepository;

    @Mock
    private TempFileStore tempFileStore;

    @Mock
    private FlowerImageStore flowerImageStore;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AnalysisService analysisService = new AnalysisService(aiClient, analysisRepository, tempFileStore, flowerImageStore);
        mockMvc = MockMvcBuilders.standaloneSetup(new AnalysisController(analysisService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAnalyses_returns_paginated_analyses_with_hasNextPage_false() throws Exception {
        Analysis analysis = Analysis.builder()
                .id(1L)
                .summary("A bouquet of love and hope")
                .content("당신의 사랑은 영원합니다")
                .story("Your love blooms eternally")
                .flowers(List.of(
                        AnalysisFlower.builder().flowerId(1L).nameKo("장미").nameEn("Rose").meaning("사랑").build()
                ))
                .createdAt(LocalDateTime.of(2026, 5, 11, 12, 0, 0))
                .build();

        given(analysisRepository.findByUserId(any(), any()))
                .willReturn(new PageImpl<>(List.of(analysis), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/analyses")
                        .requestAttr(JwtAuthFilter.USER_ID_ATTRIBUTE, 1L)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analyses").isArray())
                .andExpect(jsonPath("$.analyses.length()").value(1))
                .andExpect(jsonPath("$.analyses[0].id").value("1"))
                .andExpect(jsonPath("$.analyses[0].summary").value("A bouquet of love and hope"))
                .andExpect(jsonPath("$.analyses[0].flowerMeanings[0].nameKo").value("장미"))
                .andExpect(jsonPath("$.hasNextPage").value(false));
    }

    @Test
    void getAnalyses_hasNextPage_is_true_when_more_pages_exist() throws Exception {
        List<Analysis> analyses = List.of(
                Analysis.builder().id(1L).summary("s1").content("c1").story("st1").flowers(List.of()).createdAt(LocalDateTime.now()).build(),
                Analysis.builder().id(2L).summary("s2").content("c2").story("st2").flowers(List.of()).createdAt(LocalDateTime.now()).build()
        );

        given(analysisRepository.findByUserId(any(), any()))
                .willReturn(new PageImpl<>(analyses, PageRequest.of(0, 2), 5));

        mockMvc.perform(get("/analyses")
                        .requestAttr(JwtAuthFilter.USER_ID_ATTRIBUTE, 1L)
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analyses.length()").value(2))
                .andExpect(jsonPath("$.hasNextPage").value(true));
    }

    @Test
    void getAnalyses_uses_default_page_and_size() throws Exception {
        given(analysisRepository.findByUserId(any(), any()))
                .willReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        mockMvc.perform(get("/analyses")
                        .requestAttr(JwtAuthFilter.USER_ID_ATTRIBUTE, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analyses").isArray())
                .andExpect(jsonPath("$.analyses.length()").value(0))
                .andExpect(jsonPath("$.hasNextPage").value(false));
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

        given(tempFileStore.findByUserId(any())).willReturn(Optional.empty());

        Analysis savedAnalysis = Analysis.builder()
                .id(1L)
                .summary("A bouquet of love and hope")
                .content("당신의 사랑은 영원합니다")
                .story("Your love blooms eternally")
                .flowers(List.of(
                        AnalysisFlower.builder().flowerId(1L).nameKo("장미").nameEn("Rose").meaning("사랑").build(),
                        AnalysisFlower.builder().flowerId(2L).nameKo("튤립").nameEn("Tulip").meaning("희망").build()
                ))
                .createdAt(LocalDateTime.of(2026, 5, 11, 12, 0, 0))
                .build();

        given(analysisRepository.save(any())).willReturn(savedAnalysis);

        List<AnalysisFlowerRequest> flowerRequests = List.of(
                new AnalysisFlowerRequest("장미", "Rose", "사랑", null),
                new AnalysisFlowerRequest("튤립", "Tulip", "희망", null)
        );

        String requestBody = objectMapper.writeValueAsString(new com.flowary.server.analysis.dto.AnalysisCreateRequest(flowerRequests));

        mockMvc.perform(post("/analyses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
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

        given(tempFileStore.findByUserId(any())).willReturn(Optional.empty());

        Analysis savedAnalysis = Analysis.builder()
                .id(2L)
                .summary("Harmony")
                .content("조화로운 꽃다발")
                .story("A harmonious bouquet")
                .flowers(List.of())
                .createdAt(LocalDateTime.now())
                .build();

        given(analysisRepository.save(any())).willReturn(savedAnalysis);

        String requestBody = objectMapper.writeValueAsString(new com.flowary.server.analysis.dto.AnalysisCreateRequest(List.of()));

        mockMvc.perform(post("/analyses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.flowerMeanings").isArray())
                .andExpect(jsonPath("$.flowerMeanings.length()").value(0));
    }
}
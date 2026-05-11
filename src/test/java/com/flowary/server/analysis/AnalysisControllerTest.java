package com.flowary.server.analysis;

import com.flowary.server.ai.AiClient;
import com.flowary.server.ai.dto.AiCombineResponse;
import com.flowary.server.ai.dto.IndividualMeaning;
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

    @BeforeEach
    void setUp() {
        AnalysisService analysisService = new AnalysisService(aiClient, analysisRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(new AnalysisController(analysisService)).build();
    }

    @Test
    void createAnalysis_returns_201_with_analysis_data() throws Exception {
        given(aiClient.combineFloriography(eq(List.of("Rose", "Tulip"))))
                .willReturn(new AiCombineResponse(
                        "당신의 사랑은 영원합니다",
                        "Your love blooms eternally",
                        "A bouquet of love and hope",
                        List.of(
                                new IndividualMeaning("장미", "사랑"),
                                new IndividualMeaning("튤립", "희망")
                        )
                ));

        Analysis savedAnalysis = Analysis.builder()
                .id(1L)
                .summary("A bouquet of love and hope")
                .content("당신의 사랑은 영원합니다")
                .story("Your love blooms eternally")
                .flowers(List.of(
                        AnalysisFlower.builder().id(1L).name("장미").meaning("사랑").build(),
                        AnalysisFlower.builder().id(2L).name("튤립").meaning("희망").build()
                ))
                .createdAt(LocalDateTime.of(2026, 5, 11, 12, 0, 0))
                .build();

        given(analysisRepository.save(any())).willReturn(savedAnalysis);

        MockMultipartFile image = new MockMultipartFile(
                "image", "bouquet.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3}
        );

        mockMvc.perform(multipart("/analyses")
                        .file(image)
                        .param("flowers", "Rose", "Tulip"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.summary").value("A bouquet of love and hope"))
                .andExpect(jsonPath("$.content").value("당신의 사랑은 영원합니다"))
                .andExpect(jsonPath("$.story").value("Your love blooms eternally"))
                .andExpect(jsonPath("$.flowers").isArray())
                .andExpect(jsonPath("$.flowers.length()").value(2))
                .andExpect(jsonPath("$.flowers[0].name").value("장미"))
                .andExpect(jsonPath("$.flowers[0].meaning").value("사랑"))
                .andExpect(jsonPath("$.flowers[1].name").value("튤립"))
                .andExpect(jsonPath("$.flowers[1].meaning").value("희망"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createAnalysis_returns_empty_flowers_when_ai_returns_none() throws Exception {
        given(aiClient.combineFloriography(any()))
                .willReturn(new AiCombineResponse(
                        "조화로운 꽃다발",
                        "A harmonious bouquet",
                        "Harmony",
                        List.of()
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

        MockMultipartFile image = new MockMultipartFile(
                "image", "bouquet.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1}
        );

        mockMvc.perform(multipart("/analyses")
                        .file(image)
                        .param("flowers", "Rose"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.flowers").isArray())
                .andExpect(jsonPath("$.flowers.length()").value(0));
    }
}
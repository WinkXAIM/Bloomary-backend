package com.flowary.server.flower;

import com.flowary.server.ai.AiClient;
import com.flowary.server.ai.dto.AiDetectResponse;
import com.flowary.server.ai.dto.DetectedObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FlowerControllerTest {

    @Mock
    private AiClient aiClient;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        FlowerService flowerService = new FlowerService(aiClient);
        mockMvc = MockMvcBuilders.standaloneSetup(new FlowerController(flowerService)).build();
    }

    @Test
    void detectFlowers_returns_mapped_flowers() throws Exception {
        given(aiClient.detectFlowers(any())).willReturn(new AiDetectResponse(List.of(
                new DetectedObject("장미", "Rose", List.of(10, 20, 100, 200)),
                new DetectedObject("튤립", "Tulip", List.of(150, 30, 250, 180))
        )));

        MockMultipartFile image = new MockMultipartFile(
                "image", "bouquet.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3}
        );

        mockMvc.perform(multipart("/flowers").file(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flowers").isArray())
                .andExpect(jsonPath("$.flowers.length()").value(2))
                .andExpect(jsonPath("$.flowers[0].name").value("장미"))
                .andExpect(jsonPath("$.flowers[0].box2d[0]").value(10))
                .andExpect(jsonPath("$.flowers[0].box2d[1]").value(20))
                .andExpect(jsonPath("$.flowers[0].box2d[2]").value(100))
                .andExpect(jsonPath("$.flowers[0].box2d[3]").value(200))
                .andExpect(jsonPath("$.flowers[1].name").value("튤립"))
                .andExpect(jsonPath("$.flowers[1].box2d[0]").value(150))
                .andExpect(jsonPath("$.flowers[1].box2d[1]").value(30))
                .andExpect(jsonPath("$.flowers[1].box2d[2]").value(250))
                .andExpect(jsonPath("$.flowers[1].box2d[3]").value(180));
    }

    @Test
    void detectFlowers_returns_empty_list_when_no_flowers_detected() throws Exception {
        given(aiClient.detectFlowers(any())).willReturn(new AiDetectResponse(List.of()));

        MockMultipartFile image = new MockMultipartFile(
                "image", "empty.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1}
        );

        mockMvc.perform(multipart("/flowers").file(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flowers").isArray())
                .andExpect(jsonPath("$.flowers.length()").value(0));
    }
}
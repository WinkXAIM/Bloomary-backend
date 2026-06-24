package com.flowary.server.ai;

import com.flowary.server.ai.dto.AiCombineRequest;
import com.flowary.server.ai.dto.AiCombineResponse;
import com.flowary.server.ai.dto.AiDetectResponse;
import com.flowary.server.ai.dto.AiRecommendRequest;
import com.flowary.server.ai.dto.AiRecommendResponse;
import com.flowary.server.ai.dto.FlowerInput;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;

@Component
public class AiClient {
    private final RestClient restClient;
  
    public AiClient(AiProperties aiProperties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.restClient = RestClient.builder()
                .baseUrl(aiProperties.url())
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

    public AiDetectResponse detectFlowers(MultipartFile image) throws IOException {
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.parseMediaType(
                image.getContentType() != null ? image.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE
        ));

        HttpEntity<ByteArrayResource> filePart = new HttpEntity<>(
                new ByteArrayResource(image.getBytes()) {
                    @Override
                    public String getFilename() {
                        return image.getOriginalFilename();
                    }
                },
                fileHeaders
        );

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", filePart);

        return restClient.post()
                .uri("/ai/detect-flowers")
                .body(body)
                .retrieve()
                .body(AiDetectResponse.class);
    }

    public AiCombineResponse combineFloriography(List<FlowerInput> flowers) {
        return restClient.post()
                .uri("/ai/combine-floriography")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AiCombineRequest(flowers))
                .retrieve()
                .body(AiCombineResponse.class);
    }

    public AiRecommendResponse recommend(String userSituation) {
        return restClient.post()
                .uri("/ai/recommend-bouquet")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AiRecommendRequest(userSituation))
                .retrieve()
                .body(AiRecommendResponse.class);
    }
}
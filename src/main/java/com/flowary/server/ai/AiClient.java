package com.flowary.server.ai;

import com.flowary.server.ai.dto.AiCombineRequest;
import com.flowary.server.ai.dto.AiCombineResponse;
import com.flowary.server.ai.dto.AiDetectResponse;
import com.flowary.server.ai.dto.FlowerInput;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class AiClient {

    private final RestClient restClient;

    public AiClient(AiProperties aiProperties) {
        this.restClient = RestClient.builder()
                .baseUrl(aiProperties.url())
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
                .contentType(MediaType.MULTIPART_FORM_DATA)
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
}
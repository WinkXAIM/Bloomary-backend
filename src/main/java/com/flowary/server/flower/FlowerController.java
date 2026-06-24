package com.flowary.server.flower;

import com.flowary.server.auth.JwtAuthFilter;
import com.flowary.server.flower.dto.FlowerResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/flowers")
@RequiredArgsConstructor
public class FlowerController {

    private final FlowerService flowerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FlowerResponse detectFlowers(
            HttpServletRequest request,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        Long userId = (Long) request.getAttribute(JwtAuthFilter.USER_ID_ATTRIBUTE);
        return flowerService.detectFlowers(image, userId);
    }
}
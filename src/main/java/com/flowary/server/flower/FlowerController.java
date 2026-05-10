package com.flowary.server.flower;

import com.flowary.server.flower.dto.FlowerResponse;
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
    public FlowerResponse detectFlowers(@RequestParam("image") MultipartFile image) throws IOException {
        return flowerService.detectFlowers(image);
    }
}
package com.flowary.server.analysis;

import com.flowary.server.analysis.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AnalysisResponse createAnalysis(
            @RequestParam("image") MultipartFile image,
            @RequestParam("flowers") List<String> flowers
    ) {
        return analysisService.createAnalysis(image, flowers);
    }
}
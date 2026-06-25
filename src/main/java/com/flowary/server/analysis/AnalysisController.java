package com.flowary.server.analysis;

import com.flowary.server.analysis.dto.AnalysisCreateRequest;
import com.flowary.server.analysis.dto.AnalysisPageResponse;
import com.flowary.server.analysis.dto.AnalysisResponse;
import com.flowary.server.auth.JwtAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping
    public AnalysisPageResponse getAnalyses(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = (Long) request.getAttribute(JwtAuthFilter.USER_ID_ATTRIBUTE);
        return analysisService.getAnalyses(userId, page, size);
    }

    @GetMapping("/{id}")
    public AnalysisResponse getAnalysis(@PathVariable Long id) {
        return analysisService.getAnalysis(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnalysisResponse createAnalysis(
            HttpServletRequest request,
            @RequestBody AnalysisCreateRequest body
    ) throws java.io.IOException {
        Long userId = (Long) request.getAttribute(JwtAuthFilter.USER_ID_ATTRIBUTE);
        return analysisService.createAnalysis(userId, body.flowers());
    }
}
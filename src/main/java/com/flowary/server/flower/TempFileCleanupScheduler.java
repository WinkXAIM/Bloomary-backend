package com.flowary.server.flower;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@Slf4j
@Component
public class TempFileCleanupScheduler {

    private final TempFileStore tempFileStore;
    private final long expiryHours;

    public TempFileCleanupScheduler(TempFileStore tempFileStore,
                                    @Value("${upload.expiry-hours}") long expiryHours) {
        this.tempFileStore = tempFileStore;
        this.expiryHours = expiryHours;
    }

    @Scheduled(fixedRate = 600_000) // 10분마다 실행
    public void cleanExpiredFiles() throws IOException {
        Path tempDir = tempFileStore.getTempDir();
        Instant expireBefore = Instant.now().minus(expiryHours, ChronoUnit.HOURS);

        try (Stream<Path> files = Files.list(tempDir)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> isExpired(path, expireBefore))
                    .forEach(this::deleteQuietly);
        }
    }

    private boolean isExpired(Path path, Instant expireBefore) {
        try {
            return Files.getLastModifiedTime(path).toInstant().isBefore(expireBefore);
        } catch (IOException e) {
            return false;
        }
    }

    private void deleteQuietly(Path path) {
        try {
            Files.delete(path);
            log.debug("임시 파일 삭제: {}", path.getFileName());
        } catch (IOException e) {
            log.warn("임시 파일 삭제 실패: {}", path.getFileName(), e);
        }
    }
}
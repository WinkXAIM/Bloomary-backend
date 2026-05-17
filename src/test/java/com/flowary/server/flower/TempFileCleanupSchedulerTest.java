package com.flowary.server.flower;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TempFileCleanupSchedulerTest {

    @TempDir
    Path tempDir;

    TempFileCleanupScheduler scheduler;

    @BeforeEach
    void setUp() {
        TempFileStore tempFileStore = mock(TempFileStore.class);
        when(tempFileStore.getTempDir()).thenReturn(tempDir);

        scheduler = new TempFileCleanupScheduler(tempFileStore, 1);
    }

    @Test
    void deleteExpiredFile() throws IOException {
        Path expiredFile = tempDir.resolve("expired.jpg");
        Files.createFile(expiredFile);
        setLastModified(expiredFile, 2);

        scheduler.cleanExpiredFiles();

        assertThat(expiredFile).doesNotExist();
    }

    @Test
    void persistFreshFile() throws IOException {
        Path freshFile = tempDir.resolve("fresh.jpg");
        Files.createFile(freshFile);

        scheduler.cleanExpiredFiles();

        assertThat(freshFile).exists();
    }

    @Test
    void DeleteOnlyExpiredFile() throws IOException {
        Path expiredFile = tempDir.resolve("expired.jpg");
        Path freshFile = tempDir.resolve("fresh.jpg");
        Files.createFile(expiredFile);
        Files.createFile(freshFile);
        setLastModified(expiredFile, 2);

        scheduler.cleanExpiredFiles();

        assertThat(expiredFile).doesNotExist();
        assertThat(freshFile).exists();
    }

    @Test
    void passEmptyDir() throws IOException {
        scheduler.cleanExpiredFiles();
    }

    private void setLastModified(Path path, long hoursAgo) throws IOException {
        Instant past = Instant.now().minus(hoursAgo, ChronoUnit.HOURS);
        Files.setLastModifiedTime(path, FileTime.from(past));
    }
}
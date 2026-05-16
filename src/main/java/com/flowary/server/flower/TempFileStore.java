package com.flowary.server.flower;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class TempFileStore {

    private final Path tempDir;

    public TempFileStore(@Value("${upload.temp-dir}") String tempDirPath) throws IOException {
        this.tempDir = Paths.get(tempDirPath).toAbsolutePath();
        Files.createDirectories(this.tempDir);
    }

    public Path store(MultipartFile file) throws IOException {
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");
        Path dest = tempDir.resolve(filename);
        file.transferTo(dest);
        return dest;
    }

    public Path getTempDir() {
        return tempDir;
    }
}
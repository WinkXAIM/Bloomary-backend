package com.flowary.server.flower;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class TempFileStore {

    private final Path tempDir;

    public TempFileStore(UploadProperties uploadProperties) throws IOException {
        this.tempDir = Paths.get(uploadProperties.tempDir()).toAbsolutePath();
        Files.createDirectories(this.tempDir);
    }

    public Path store(MultipartFile file, Long userId) throws IOException {
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = userId + (ext != null ? "." + ext : "");
        Path dest = tempDir.resolve(filename);
        file.transferTo(dest);
        return dest;
    }

    public Optional<Path> findByUserId(Long userId) throws IOException {
        String prefix = userId + ".";
        try (Stream<Path> files = Files.list(tempDir)) {
            return files.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().startsWith(prefix))
                    .findFirst();
        }
    }

    public Path getTempDir() {
        return tempDir;
    }
}
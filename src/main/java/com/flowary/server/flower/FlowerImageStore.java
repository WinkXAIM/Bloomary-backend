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
public class FlowerImageStore {

    private final Path flowersDir;

    public FlowerImageStore(@Value("${upload.flowers-dir}") String flowersDirPath) throws IOException {
        this.flowersDir = Paths.get(flowersDirPath).toAbsolutePath();
        Files.createDirectories(this.flowersDir);
    }

    public Path store(MultipartFile file) throws IOException {
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");
        Path dest = flowersDir.resolve(filename);
        file.transferTo(dest);
        return dest;
    }

    public Path getFlowersDir() {
        return flowersDir;
    }
}
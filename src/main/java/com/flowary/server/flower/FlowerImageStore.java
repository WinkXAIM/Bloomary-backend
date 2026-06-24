package com.flowary.server.flower;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FlowerImageStore {

    private final Path flowersDir;

    public FlowerImageStore(UploadProperties uploadProperties) throws IOException {
        this.flowersDir = Paths.get(uploadProperties.flowersDir()).toAbsolutePath();
        Files.createDirectories(this.flowersDir);
    }

    public String moveFromTemp(Path tempPath) throws IOException {
        String ext = StringUtils.getFilenameExtension(tempPath.getFileName().toString());
        String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");
        Path dest = flowersDir.resolve(filename);
        Files.move(tempPath, dest, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    public Path getFlowersDir() {
        return flowersDir;
    }
}
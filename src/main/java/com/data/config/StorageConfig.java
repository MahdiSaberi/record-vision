package com.data.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class StorageConfig {
    private final String tempDir = System.getProperty("java.io.tmpdir");
    @Value("${database.file.name}")
    private String dbFileName;
    @Bean
    public void createDataStorageFile() throws IOException {
        Path path = Path.of(tempDir,dbFileName);
        if(!Files.exists(path)) {
            Files.createFile(path);
        }
    }
}

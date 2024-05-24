package com.samsungcloud;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "directory")
public class DirectoryConfig {
    private String base;
    private int maxFileSize;
    private int filePollingSec;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public int getFilePollingSec() {
        return filePollingSec;
    }

    public void setFilePollingSec(int filePollingSec) {
        this.filePollingSec = filePollingSec;
    }
}
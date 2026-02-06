package com.example.hello_spring.cv.dto;

import org.springframework.core.io.Resource;

public class FileDownload {

    private final Resource resource;
    private final String fileName;
    private final String contentType;

    public FileDownload(Resource resource, String fileName, String contentType) {
        this.resource = resource;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public Resource getResource() {
        return resource;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }
}

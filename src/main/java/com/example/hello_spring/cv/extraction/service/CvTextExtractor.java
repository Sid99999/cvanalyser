package com.example.hello_spring.cv.extraction.service;

import java.io.File;

public interface CvTextExtractor {

    boolean supports(String fileType);

    String extractText(File file);
}

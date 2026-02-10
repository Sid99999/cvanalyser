package com.example.hello_spring.cv.extraction.service;

import com.example.hello_spring.cv.extraction.exception.CvTextExtractionException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class CvTextExtractionService {

    private final List<CvTextExtractor> extractors;

    public CvTextExtractionService(List<CvTextExtractor> extractors) {
        this.extractors = extractors;
    }

    public String extractText(File file, String fileType) {

        return extractors.stream()
                .filter(extractor -> extractor.supports(fileType))
                .findFirst()
                .orElseThrow(() ->
                        new CvTextExtractionException(
                                "Unsupported CV file type: " + fileType
                        )
                )
                .extractText(file);
    }
}

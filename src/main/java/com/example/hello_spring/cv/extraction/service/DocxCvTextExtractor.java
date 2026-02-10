package com.example.hello_spring.cv.extraction.service;

import com.example.hello_spring.cv.extraction.exception.CvTextExtractionException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;

@Component
public class DocxCvTextExtractor implements CvTextExtractor {

    @Override
    public boolean supports(String fileType) {
        return fileType.equalsIgnoreCase(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );
    }

    @Override
    public String extractText(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            return extractor.getText();
        } catch (Exception ex) {
            throw new CvTextExtractionException("Failed to extract text from DOCX", ex);
        }
    }
}

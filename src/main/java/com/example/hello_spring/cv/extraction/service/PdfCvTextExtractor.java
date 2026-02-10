package com.example.hello_spring.cv.extraction.service;

import com.example.hello_spring.cv.extraction.exception.CvTextExtractionException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PdfCvTextExtractor implements CvTextExtractor {

    @Override
    public boolean supports(String fileType) {
        return fileType.equalsIgnoreCase("application/pdf");
    }

    @Override
    public String extractText(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception ex) {
            throw new CvTextExtractionException("Failed to extract text from PDF", ex);
        }
    }
}

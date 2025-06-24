package com.bankai.bankai.prompt;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.ai.reader.tika.TikaDocumentReader;

@Component
class DocumentReader {

    private final Resource resource;

    DocumentReader() {
        String filePath = "C:/Users/nelio/Documents/Regulamentograd.pdf";
        this.resource =  new FileSystemResource(filePath);       
    }

    List<Document> loadText() {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(this.resource);
        return tikaDocumentReader.read();
    }
}

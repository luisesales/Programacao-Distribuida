package com.bankai.stock.prompt;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class DocumentReader {

    private final Resource resource;

    DocumentReader() {
        String filePath = "C:/Users/nelio/Documents/Regulamentograd.pdf";
        this.resource =  new FileSystemResource(filePath);       
    }

    public List<Document> loadText() {
        // TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(this.resource);
        // return tikaDocumentReader.read();
        String exemplo = """
        Este é um regulamento fictício utilizado apenas para testes da aplicação.
        O aluno pode trancar a matrícula em até dois períodos consecutivos.
        A carga horária mínima por semestre é de 180 horas.
        """;

        Document doc = new Document(exemplo);
        return List.of(doc);
    }
}

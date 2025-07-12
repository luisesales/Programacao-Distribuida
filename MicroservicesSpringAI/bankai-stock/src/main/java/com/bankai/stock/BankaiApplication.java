package com.bankai.stock;

import com.bankai.stock.model.BankAI;
import com.bankai.stock.prompt.DocumentReader;

import java.util.List;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.ai.document.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BankaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankaiApplication.class, args);
	}
	
	@Bean
	public VectorStore vectorStore(EmbeddingModel embeddingModel) {
		return SimpleVectorStore.builder(embeddingModel).build();
	}

	@Bean
    public CommandLineRunner initVectorStore(DocumentReader documentReader, BankAI bankAI) {
        return args -> {
            System.out.println("Iniciando a ingestão de documentos para o VectorStore...");
            List<Document> loadedDocuments = documentReader.loadText();

            TokenTextSplitter textSplitter = new TokenTextSplitter();
            List<Document> chunks = textSplitter.apply(loadedDocuments);

            bankAI.add(chunks);
            System.out.println("Documentos ingeridos no VectorStore com sucesso!");
        };
    }

}

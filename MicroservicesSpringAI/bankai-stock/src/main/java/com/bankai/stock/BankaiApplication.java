package com.bankai.stock;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import io.modelcontextprotocol.spec.McpSchema;

import com.bankai.stock.model.BankAI;
import com.bankai.stock.prompt.DocumentReader;

import io.modelcontextprotocol.client.McpSyncClient;

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
    public CommandLineRunner initVectorStore(DocumentReader documentReader, BankAI bankAI,List<McpSyncClient> clients) {
        return args -> {
            System.out.println("Iniciando a ingestão de documentos para o VectorStore...");
            List<Document> loadedDocuments = documentReader.loadText();

            TokenTextSplitter textSplitter = new TokenTextSplitter();
            List<Document> chunks = textSplitter.apply(loadedDocuments);

            bankAI.add(chunks);
            System.out.println("Documentos ingeridos no VectorStore com sucesso!");

            McpSyncClient syncClientS1 = clients.get(0);
            //McpSyncClient syncClientS2 = clients.get(1);

            McpSchema.ListToolsResult listToolsResultS1 = syncClientS1.listTools();
            //McpSchema.ListToolsResult listToolsResultS2 = syncClientS2.listTools();
            listToolsResultS1.tools().stream().map(McpSchema.Tool::name).forEach(System.out::println);
            //listToolsResultS2.tools().stream().map(McpSchema.Tool::name).forEach(System.out::println);

            McpSchema.CallToolResult balance = syncClientS1.callTool(new McpSchema.CallToolRequest("balance")) 


        };
    }
}

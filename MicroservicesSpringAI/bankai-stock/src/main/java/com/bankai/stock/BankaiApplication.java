package com.bankai.stock;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

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

}

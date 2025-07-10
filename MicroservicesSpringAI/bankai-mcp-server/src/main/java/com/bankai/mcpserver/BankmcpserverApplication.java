package com.bankai.mcpserver;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils.MethodCallback;

import java.utils.List;

import com.bankai.mcpserver.tools.BankAITools;

@SpringBootApplication
public class BankmcpserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankmcpserverApplication.class, args);
	}

	// @Bean
	// public VectorStore vectorStore(EmbeddingModel embeddingModel) {
	// 	return SimpleVectorStore.builder(embeddingModel).build();
	// }

	@Bean
	public List<ToolCallback> regBankAITools(BankAITools mcpServer){
	return List.of(ToolCalbacks.from(mcpServer));
	}		
}

package com.bankai.mcpserver.application;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils.MethodCallback;

import com.bankai.mcpserver.mcpserver.BankAIServer;

@SpringBootApplication
public class BankmcpserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankmcpserverApplication.class, args);
	}

	/*@Bean
	public VectorStore vectorStore(EmbeddingModel embeddingModel) {
		return SimpleVectorStore.builder(embeddingModel).build();
	}

	@Bean
	public ToolCallbackProvider regBankAITools(BankAISer mcpServer){
		return MethodCallbackProvider.builder.toolObjects(mcpServer).build();
	}*/
	@Bean
	public List<ToolCallbackProvider> 
		
}

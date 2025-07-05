package com.bankai.mcpserver.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils.MethodCallback;

@SpringBootApplication
public class BankmcpserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankmcpserverApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider regBankAITools(BankAITools mcpServer){
		return MethodCallbackProvider.builder.toolObjects(mcpServer).build();
	}
		
}

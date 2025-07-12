package com.bankai.mcpserver;

import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.bankai.mcpserver.tools.BankAITools;

@SpringBootApplication
public class BankmcpserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankmcpserverApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider aiTools(){
		return MethodToolCallbackProvider.builder().toolObjects(new BankAITools()).build();
	}
}

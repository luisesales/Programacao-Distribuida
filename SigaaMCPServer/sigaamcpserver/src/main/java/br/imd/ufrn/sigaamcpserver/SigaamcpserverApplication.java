package br.imd.ufrn.sigaamcpserver;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import br.imd.ufrn.sigaamcpserver.tools.SigaaTools;

@SpringBootApplication
public class SigaamcpserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(SigaamcpserverApplication.class, args);
	}
	 @Bean
    public ToolCallbackProvider regSigaaTools(SigaaTools  mcpServer) {
        return MethodToolCallbackProvider.builder().toolObjects(mcpServer).build();
    }

}

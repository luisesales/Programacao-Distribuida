package br.imd.ufrn.prompt;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;

@Service
@Primary
public class OpenAIChatService implements ChatService {
    private final ChatClient chatClient;

    @Value("classpath:prompt/promptUser.st")
    Resource templateuser;

    @Value("classpath:prompt/promptSystem.st")
    Resource templatesystem;

    public OpenAIChatService(ChatClient.Builder chatClientBuilderOpenAI) {
        ChatOptions chatOptions = ChatOptions.builder().model("gpt-4.1").build();
        this.chatClient = chatClientBuilderOpenAI.defaultOptions(chatOptions).build();
    }

    @Override
    public String getResposta(String pergunta) {
        var responseEntity = chatClient.prompt()
        .system(systemSpec -> systemSpec
                        .text(templatesystem) 
                        .param("Universidade", "UFRN"))   
        .user(userSpec -> userSpec
                        .text(templateuser) 
                        .param("Pergunta", pergunta)) 
                .call()
                .responseEntity(String.class);
                ChatResponse response = responseEntity.response();
                ChatResponseMetadata metadata = response.getMetadata();

                System.out.println("Metadados de Uso: " + metadata.getUsage());
                return responseEntity.entity();

    }
    @Override
    public List<Reitores> getReitores(String pergunta) {
        return chatClient.prompt()      
        .user(pergunta)
                .call()
                .entity(new ParameterizedTypeReference<List<Reitores>>() {});

    }
     public Reitores getReitor(String pergunta) {
        return chatClient.prompt()      
        .user(pergunta)
                .call()
                .entity(Reitores.class);
    }

}

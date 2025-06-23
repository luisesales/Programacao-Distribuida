package br.imd.ufrn.prompt;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class OllamaChatService implements ChatService {
    private final ChatClient chatClient;

   public OllamaChatService(ChatClient.Builder chatClientBuilderOllamaBuilder) {
       ChatOptions chatOptions = ChatOptions.builder().model("gemma3:12b").build();
       this.chatClient = chatClientBuilderOllamaBuilder.defaultOptions(chatOptions).build();
   }

    @Override
    public String getResposta(String pergunta) {
        return chatClient.prompt().user(pergunta).call().content();
    }
}

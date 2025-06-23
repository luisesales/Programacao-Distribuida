package br.imd.ufrn.prompt;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

@Service
public class OpenAIChatService implements ChatService {
    private final ChatClient chatClient;

   public OpenAIChatService(ChatClient.Builder chatClientBuilderOpenAI) {
       ChatOptions chatOptions = ChatOptions.builder().model("gpt-4o-mini").build();
       this.chatClient = chatClientBuilderOpenAI.defaultOptions(chatOptions).build();
   }

    @Override
    public String getResposta(String pergunta) {
        return chatClient.prompt().user(pergunta).call().content();
    }
}

 

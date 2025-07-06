package com.bankai.stock.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.bankai.stock.interfaces.ChatService;
import com.google.api.client.util.Value;

@Service
public class BankAIService implements ChatService {
    private final ChatClient chatClient;
    private RelevancyEvaluator evaluator;
    private final DocumentReader documentReader;

    @Value("classpath:prompt/systemTemplate.st")
    Resource systemTemplate;

    @Value("classpath:prompt/userTemplate.st")
    Resource userTemplate;

    @Autowired
    VectorStore vectorStore;

    ChatMemoryRepository chatMemoryRepository = new InMemoryChatMemoryRepository();
    ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(10)
            .build();


    public BankAIService(ChatClient.Builder chatClientBuilderOpenAI,  DocumentReader documentReader){
        
        ChatOptions chatOptions = ChatOptions.builder().model("gpt-3.5-turbo").build();        
        this.chatClient = chatClientBuilderOpenAI.defaultOptions(chatOptions).build();
        this.documentReader = documentReader;
        evaluator = new RelevancyEvaluator(chatClientBuilderOpenAI);

    }
    @Override
    public String getAnswer(String prompt) {        
        String answer = chatClient.prompt()
            .system(systemSpec -> systemSpec 
                .text(systemTemplate)
                .param("Banco", "BankAI"))
            .user(userSpec -> userSpec
                .text(userTemplate)
                .param("Pergunta", prompt))
            .call().content();
        EvaluationRequest request = new EvaluationRequest(prompt, null, answer);
        EvaluationResponse response = evaluator.evaluate(request);
        if(!response.isPass())
            return "Não posso lhe ajudar com isso no momento, posso lhe ajudar com outra coisa?";
        return answer;
    }
}
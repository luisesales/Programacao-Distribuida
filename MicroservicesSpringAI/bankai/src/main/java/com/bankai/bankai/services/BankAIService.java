package com.bankai.bankai.services;

import java.lang.module.ModuleDescriptor.Builder;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;

import com.bankai.bankai.interfaces.ChatService;

public class BankAIService implements ChatService {
    private final ChatClient chatClient;
    private RelevancyEvaluator evaluator;

    public BankAIService(ChatClient.Builder chatClientBuilderOpenAI){
        ChatOptions chatOptions = ChatOptions.builder().model("gpt-3.5").build();        
        this.chatClient = chatClientBuilderOpenAI.defaultOptions(chatOptions).build();
        evaluator = new RelevancyEvaluator(chatClientBuilderOpenAI);

    }
    @Override
    public String getAnswer(String prompt) {
        String answer = chatClient.prompt().user(prompt).call().content();
        EvaluationRequest request = new EvaluationRequest(prompt, null, answer);
        EvaluationResponse response = evaluator.evaluate(request);
        if(!response.isPass())
            return "Não posso lhe ajudar com isso no momento, posso lhe ajudar com outra coisa?";
        return answer;
    }
}
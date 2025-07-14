package com.bankai.stock.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.bankai.stock.interfaces.ChatServiceAi;
import com.bankai.stock.model.BankAI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BankAIService implements ChatServiceAi {
    private final ChatClient chatClient;
    private final BankAI bankAI;
    // private RelevancyEvaluator evaluator;


    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Value("classpath:prompt/systemTemplate.st")
    Resource systemTemplateResource;

    @Value("classpath:prompt/userTemplate.st")
    Resource userTemplateResource;
    
    ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .maxMessages(10)
            .build();


    public BankAIService(ChatModel chatModel, BankAI bankAI, ToolCallbackProvider tools, ChatMemory chatMemory){     
        this.chatClient = ChatClient.builder(chatModel)
            .defaultToolCallbacks(tools)
            .defaultAdvisors(
                    MessageChatMemoryAdvisor.builder(chatMemory).build())
        .build();
        this.bankAI = bankAI;
        // this.evaluator = RelevancyEvaluator.builder()
        //                     .chatClientBuilder(ChatClient.builder(chatModel))
        //                     .build();
    }

    @Override
    public String getAnswer(String prompt) {
        List<Document> relatedDocuments = bankAI.findClosestMatches(prompt, 3);
        if (relatedDocuments == null || relatedDocuments.isEmpty()) {
            return "Desculpe, não encontrei informações relevantes para sua pergunta.";
        }

        String context = relatedDocuments.stream()
                            .map(Document::getText)
                            .collect(Collectors.joining("\n---\n"));

        String systemTemplate = readResourceToString(systemTemplateResource);
        String userTemplate = readResourceToString(userTemplateResource);

        if (systemTemplate.isEmpty() || userTemplate.isEmpty()) {
            return "Erro interno: templates não encontrados.";
        }

        // System.out.println("systemTemplate: " + systemTemplate);
        // System.out.println("userTemplate: " + userTemplate);
        // System.out.println("Contexto: " + context);
        // System.out.println("Prompt: " + prompt);


        String answer = "";
        try {
            System.out.println("Sending prompt to AI model...");

            answer = chatClient.prompt()
                .system(systemSpec -> systemSpec
                    .text(systemTemplate)
                    .param("Banco", "BankAI")
                    .param("Contexto", context))
                .user(userSpec -> userSpec
                    .text(userTemplate)
                    .param("Pergunta", prompt))
                .call()
                .content();
                
        } catch (Exception springAiException) {
            System.out.println("Spring AI falhou. Fallback para WebClient. Erro:");
            springAiException.printStackTrace();

            answer = callOpenAiDirectly(systemTemplate, userTemplate, context, prompt);
            if (answer == null) {
                return "Desculpe, estou com problemas para me conectar ao serviço de IA no momento.";
            }
        }

        // EvaluationRequest request = new EvaluationRequest(prompt, relatedDocuments, answer);
        // try {
        //     EvaluationResponse response = evaluator.evaluate(request);
        //     if (!response.isPass()) {
        //         return "Não posso lhe ajudar com isso no momento, posso ajudar com outra coisa?";
        //     }
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        //     return "Erro ao processar a resposta da IA. Tente novamente mais tarde.";
        // } finally {
        //     chatMemory.add("default", new AssistantMessage(answer));
        // }

        chatMemory.add("default", new AssistantMessage(answer));

        return answer;
    }

    private String callOpenAiDirectly(String systemTemplate, String userTemplate, String context, String prompt) {
        WebClient client = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        try {
            String response = client.post()
                    .bodyValue(Map.of(
                            "model", "gpt-4", // ou "gpt-4", "gpt-4o" conforme seu plano
                            "messages", List.of(
                                    Map.of("role", "system", "content", fillTemplate(systemTemplate, context, prompt)),
                                    Map.of("role", "user", "content", fillTemplate(userTemplate, context, prompt))
                            )
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Resposta bruta da OpenAI:");
            System.out.println(response);

            return extractContentFromJson(response);

        } catch (Exception e) {
            System.out.println("Erro ao chamar diretamente a API da OpenAI:");
            e.printStackTrace();
            return null;
        }
    }

    private String fillTemplate(String template, String context, String prompt) {
        return template
                .replace("${Banco}", "BankAI")
                .replace("${Contexto}", context)
                .replace("${Pergunta}", prompt);
    }

    private String extractContentFromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode content = choices.get(0).path("message").path("content");
                if (!content.isMissingNode()) {
                    return content.asText();
                }
            }
            return "IA não retornou resposta válida.";
        } catch (Exception e) {
            System.out.println("Erro ao extrair conteúdo da resposta JSON:");
            e.printStackTrace();
            return "Erro ao interpretar a resposta da IA.";
        }
    }

    private String readResourceToString(Resource resource) {
        if (resource == null || !resource.exists()) {
            System.out.print("Resource does not exist: {" + resource + "}");
            return "";
        }
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            return new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.print("Failed to read resource: {" + resource + "}. " + e.getMessage());
            return "";
        }
    }
}
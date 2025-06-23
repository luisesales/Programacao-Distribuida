package br.imd.ufrn.embedding.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import br.imd.ufrn.embedding.dao.CursosDAO;

@Service
public class CursosServiceImpl implements CursosService {
    private final CursosDAO cursosDAO;
    private final DocumentReader documentReader;

    @Autowired
    VectorStore vectorStore;

    ChatMemoryRepository chatMemoryRepository = new InMemoryChatMemoryRepository();
    ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(10)
            .build();

    private final ChatClient chatClient;

    @Value("classpath:prompt/promptUser.st")
    Resource templateuser;

    @Value("classpath:prompt/promptSystem.st")
    Resource templatesystem;

    public CursosServiceImpl(CursosDAO cursosDAO, DocumentReader documentReader,
            ChatClient.Builder chatClientBuilderOpenAI) {
        this.cursosDAO = cursosDAO;
        this.documentReader = documentReader;
        this.chatClient = chatClientBuilderOpenAI
                .build();

    }

    @Override
    public List<String> findClosestMatches(String query) {
        return cursosDAO.findClosestMatches(query, 5);
    }

    @Override
    public String findClosestMatch(String query) {
        return cursosDAO.findClosestMatch(query);
    }

    @Override
    public void loadDocument() {
        List<Document> documents = documentReader.loadText();
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chucksdocs = splitter.apply(documents);
        // Adiciona os documentos ao VectorStore
        cursosDAO.add(chucksdocs);
    }

    @Override
    public String getResposta(String pergunta, String usuario) {
        String conversationId = "conversation-" + usuario;
       
        return chatClient.prompt()
                .advisors(
                    PromptChatMemoryAdvisor.builder(chatMemory).build(),
                    QuestionAnswerAdvisor.builder((vectorStore)).build()) 
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .system(systemSpec -> systemSpec
                        .text(templatesystem)
                        .param("Universidade", "UFRN"))
                .user(userSpec -> userSpec
                        .text(templateuser)
                        .param("Pergunta", pergunta))
                .call()
                .content();

    }

}

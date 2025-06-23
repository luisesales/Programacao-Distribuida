package br.imd.ufrn.prompt;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PromptController {
    private final ChatService chatService;

    public PromptController(ChatService chatService) {
        this.chatService = chatService;
    }
    @GetMapping("prompt")
    public String getResposta(@RequestParam String pergunta) {
        return chatService.getResposta(pergunta);
    }
      @GetMapping("reitores")
    public List<Reitores> getReitores(@RequestParam String pergunta) {
        return chatService.getReitores(pergunta);
    }
    
}

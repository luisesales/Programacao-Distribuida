package br.imd.ufrn.embedding;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class EmbeddingController {
    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }
    @GetMapping("embedding")
    public String getResposta(@RequestParam String pergunta) {
        return embeddingService.findCurso(pergunta);
    }
    
}

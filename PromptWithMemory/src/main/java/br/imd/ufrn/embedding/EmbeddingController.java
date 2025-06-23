package br.imd.ufrn.embedding;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.imd.ufrn.embedding.service.CursosServiceImpl;



@RestController
public class EmbeddingController {
    private final CursosServiceImpl embeddingService;

    public EmbeddingController(CursosServiceImpl embeddingService) {
        this.embeddingService = embeddingService;
    }
    @GetMapping("embedding")
    public String getConsultaSemantica(@RequestParam String pergunta) {
        return embeddingService.findClosestMatch(pergunta);
    }

    @GetMapping("consulta")
    public String getConsultaRAG(@RequestParam String pergunta, @RequestParam String usuario) {
        return embeddingService.getResposta(pergunta, usuario);
    }

    @GetMapping("/")
    public String abrirpágina() {
        return embeddingService.getResposta("Quem é você? Se apresente para mim","Usuário");
    }

    @GetMapping("addregras")
    public void getResposta() {
        embeddingService.loadDocument();
    }
    
}

package br.imd.ufrn.embedding;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.imd.ufrn.embedding.service.CursosServiceImpl;



@RestController
public class EmbeddingController {
    private final CursosServiceImpl embeddingService;

     private List<String> cursos = List.of(
            "Medicina: Formação Generalista e Humanista: Foco em capacitar o profissional para atuar em diversas áreas da medicina, com forte ênfase na atenção primária, saúde coletiva e aspectos éticos e sociais da profissão.\n" + //
                                "Longa Duração e Intensidade: É um curso integral de 6 anos, com alta carga horária teórica e prática.\n" + //
                                "Prática Clínica e Estágios: Inclui estágios supervisionados em hospitais (como o Hospital Universitário Onofre Lopes - HUOL), unidades de saúde e outros cenários de prática desde os primeiros anos.\n" + //
                                "Pesquisa: Incentivo à participação em projetos de iniciação científica e ligas acadêmicas.\n" + //
                                "Alta Concorrência: Historicamente um dos cursos mais disputados no SiSU.",
            "Direito: Formação em Ciências Jurídicas e Sociais: Aborda os fundamentos do direito (constitucional, civil, penal, administrativo, etc.), a teoria do Estado e a organização social.\n" + //
                                "Múltiplas Áreas de Atuação: Prepara para a advocacia, magistratura, Ministério Público, defensoria pública, diplomacia, consultoria jurídica em empresas e órgãos públicos, entre outros.\n" + //
                                "Habilidades Críticas e Argumentativas: Desenvolvimento da capacidade de análise, interpretação de leis, argumentação e escrita jurídica.\n" + //
                                "Pesquisa e Extensão: Incentivo à pesquisa em diversas áreas do direito e participação em projetos de extensão universitária que levam o conhecimento jurídico à comunidade.\n" + //
                                "Duração: 5 anos (bacharelado).",
            "Engenharia Elétrica: Formação Abrangente em Eletricidade: Foco em sistemas de potência (geração, transmissão e distribuição de energia), telecomunicações, controle e automação, e eletrônica.\n" + //
                                "Base Sólida em Exatas: Grande ênfase em matemática, física e computação.\n" + //
                                "Habilidades Analíticas e de Projeto: Capacidade de conceber, projetar, implementar e manter sistemas e dispositivos elétricos/eletrônicos.\n" + //
                                "Atuação Diversificada: Campo de trabalho em indústrias, empresas de energia, telecomunicações, automação, pesquisa e desenvolvimento.\n" + //
                                "Duração: 5 anos (bacharelado).",
            "Ciência da Computação:Fundamentos Teóricos e Práticos: Abrange algoritmos, estruturas de dados, linguagens de programação, arquitetura de computadores, sistemas operacionais, redes e inteligência artificial.\n" + //
                                "Pensamento Computacional: Desenvolvimento da capacidade de resolver problemas de forma lógica e eficiente, usando ferramentas computacionais.\n" + //
                                "Desenvolvimento de Software e Hardware: Prepara para atuar no desenvolvimento de sistemas, softwares, jogos, aplicativos, segurança da informação, entre outros.\n" + //
                                "Inovação e Pesquisa: Fortemente voltado para a pesquisa e inovação, com possibilidades de atuação em polos tecnológicos e instituições de P&D.\n" + //
                                "Duração: Aproximadamente 4 a 4,5 anos (bacharelado). A UFRN também oferece o Bacharelado em Tecnologia da Informação (BTI) como um ciclo inicial para Ciência da Computação, Engenharia de Software e Engenharia de Computação."
    );

    public EmbeddingController(CursosServiceImpl embeddingService) {
        this.embeddingService = embeddingService;
    }
    @GetMapping("embedding")
    public String getResposta(@RequestParam String pergunta) {
        return embeddingService.findClosestMatch(pergunta);
    }

    @GetMapping("addcurso")
    public void getResposta() {
        embeddingService.save(cursos);
    }
    
}

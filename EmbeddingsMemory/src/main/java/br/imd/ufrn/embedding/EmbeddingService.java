package br.imd.ufrn.embedding;

import java.util.Arrays;
import java.util.List;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private final EmbeddingModel model;

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
    public EmbeddingService(EmbeddingModel model) {
        this.model = model;
    }
    public String findCurso(String query) {

        List<float[]> cursoEmbeddings = null;
        float[] queryEmbedding = null;

        cursoEmbeddings = model.embed(cursos); // Embedding dos cursos
        queryEmbedding = model.embed(query);  // Embedding da query

        int mostSimilarIndex = -1;
        mostSimilarIndex = findColosestMatch(queryEmbedding, cursoEmbeddings); //
        
        if(mostSimilarIndex < 0) {
            return "Nenhum curso encontrado para a consulta: " + query;
        } else {
            return cursos.get(mostSimilarIndex);
        }
    }
    public static int findColosestMatch(float[] query, List<float[]> cursos) {
        int mostSimilarIndex = -1;
        double mostSimilarScore = -1;
        for (int i = 0; i < cursos.size(); i++) {
            float[] cursosEmbedding = cursos.get(i);
            double similarity = cosineSimilarity(query, cursosEmbedding);
            if (similarity > mostSimilarScore) {
                mostSimilarScore = similarity;
                mostSimilarIndex = i;
            }
        }
        return mostSimilarIndex;
    }
    public static double cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            double a = vectorA[i];
            double b = vectorB[i];
            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

       
        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}
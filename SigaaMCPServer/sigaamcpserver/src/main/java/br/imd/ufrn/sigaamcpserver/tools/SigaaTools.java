package br.imd.ufrn.sigaamcpserver.tools;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import br.imd.ufrn.sigaamcpserver.service.DiscentesService;

@Component
public class SigaaTools {

    private final DiscentesService discentesService;

    public SigaaTools(DiscentesService discentesService) {
        this.discentesService = discentesService;
    }

    @Tool(name = "matricularDiscente", description = "Matrícular um discente em uma turma")
    public String matricularDiscente(String discente, String turma) {
        return discentesService.matricularDiscente(discente, turma);
    }

    @Tool(name = "cancelarMatriculaDiscente", description = "Cancela a matrícula de um discente em uma turma")
    public String cancelarMatriculaDiscente(String discente, String turma) {
        return discentesService.cancelarMatriculaDiscente(discente, turma);
    }

    @Tool(name = "listarDiscentesMatriculados", description = "Lista os discentes matriculados em uma turma")
    public List<String> listarDiscentesMatriculados(String turma) {
        return discentesService.listarDiscentesMatriculados(turma);
    }

    @Tool(name = "contarVagasDisponiveis", description = "verificar o número de vagas disponíveis em uma turma")
    public int contarVagasDisponiveis(String turma) {
        return discentesService.contarVagasDisponiveis(turma);
    }
}

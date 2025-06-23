package br.imd.ufrn.sigaamcpserver.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;


    @Service
    public class DiscentesService {
        public String matricularDiscente(String discente, String turma) {
            // Simula a matrícula do discente na turma
            return "Discente " + discente + " matriculado na turma " + turma;
        }
        public String cancelarMatriculaDiscente(String discente, String turma) {
            // Simula a desmatrícula do discente na turma
            return "Discente " + discente + " desmatriculado da turma " + turma;
        }
        public List<String> listarDiscentesMatriculados(String turma) {
            // Simula a listagem de discentes matriculados na turma
            List<String> discentes = new ArrayList<>();
            discentes.add("2001123456");
            discentes.add("2001123457");    
            return discentes;
        }
       
        public int contarVagasDisponiveis(String turma) {
            // Simula a contagem de vagas disponíveis na turma
            return 5; // Retorna o número de vagas disponíveis
        }

       
    }



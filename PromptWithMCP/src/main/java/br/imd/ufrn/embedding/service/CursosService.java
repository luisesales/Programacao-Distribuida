package br.imd.ufrn.embedding.service;

import java.util.List;


public interface CursosService {
    void loadDocument();
    List<String> findClosestMatches(String query);
    String findClosestMatch(String query);
    public String getResposta(String pergunta, String usuario);
}

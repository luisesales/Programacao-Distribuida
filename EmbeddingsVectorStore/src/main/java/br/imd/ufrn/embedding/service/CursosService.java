package br.imd.ufrn.embedding.service;

import java.util.List;

public interface CursosService {
     void save(List<String> cursos);
    List<String> findClosestMatches(String query);
    String findClosestMatch(String query);
}

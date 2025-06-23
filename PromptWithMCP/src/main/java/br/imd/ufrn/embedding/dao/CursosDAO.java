package br.imd.ufrn.embedding.dao;

import java.util.List;

import org.springframework.ai.document.Document;

public interface CursosDAO {
    public void add(List<Document> chucks);
    List<String> findClosestMatches(String query,int numberOfMatches);
    String findClosestMatch(String query);
}

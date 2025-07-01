package com.bankai.stock.dao;

import java.util.List;


import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.ai.document.Document;

@Repository
public class BankAIDAO{
     @Autowired VectorStore vectorStore;
   
     public void add(List<Document> chuncks) {
         vectorStore.add(chuncks);
    }
    public List<String> findClosestMatches(String query,int numberOfMatches) {
        SearchRequest request = SearchRequest.builder()
            .query(query)
            .topK(numberOfMatches)
            .build();
        List<Document> results = vectorStore.similaritySearch(request);
        if (results == null) {
            return List.of();
        }
        return results.stream()
            .map((Document doc) -> doc.getText())
            .toList();
    }
    public String findClosestMatch(String query) {
        return findClosestMatches(query, 1).get(0);            
    }

}

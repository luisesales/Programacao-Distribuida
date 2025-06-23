package br.imd.ufrn.embedding.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.imd.ufrn.embedding.dao.CursosDAO;
@Service
public class CursosServiceImpl implements CursosService {
    private final CursosDAO cursosDAO;
    public CursosServiceImpl(CursosDAO cursosDAO) {
        this.cursosDAO = cursosDAO;
    }
    @Override
    public void save(List<String> cursos) {
        cursosDAO.add(cursos);
    }
    @Override
    public List<String> findClosestMatches(String query) {
        return cursosDAO.findClosestMatches(query, 5);
    }
    @Override
    public String findClosestMatch(String query) {
        return cursosDAO.findClosestMatch(query);
    }

}

package br.imd.ufrn.prompt;

import java.util.List;

public interface ChatService {
    String getResposta(String pergunta);
    List<Reitores> getReitores(String pergunta);
}

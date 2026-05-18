package service;

import model.Troca;
import repository.TrocaRepository;
import java.util.List;

public class TrocaService {
    private final TrocaRepository trocaRepository;

    public TrocaService(TrocaRepository trocaRepository) {
        this.trocaRepository = trocaRepository;
    }

    public Troca realizarTroca(Troca troca) {
        troca.setStatus("ACEITA");
        return trocaRepository.save(troca);
    }

    public List<Troca> listarTrocas() {
        return trocaRepository.findAll();
    }
}

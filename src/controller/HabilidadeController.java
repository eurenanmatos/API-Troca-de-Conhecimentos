package controller;

import model.Habilidade;
import repository.HabilidadeRepository;
import java.util.List;

public class HabilidadeController {
    private final HabilidadeRepository habilidadeRepository;

    public HabilidadeController(HabilidadeRepository habilidadeRepository) {
        this.habilidadeRepository = habilidadeRepository;
    }

    public Habilidade criarHabilidade(Habilidade habilidade) {
        return habilidadeRepository.save(habilidade);
    }

    public Habilidade buscarPorId(Long id) {
        return habilidadeRepository.findById(id);
    }

    public List<Habilidade> listarHabilidades() {
        return habilidadeRepository.findAll();
    }
}

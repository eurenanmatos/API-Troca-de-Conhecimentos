package service;

import model.Habilidade;
import repository.HabilidadeRepository;

import java.util.List;

public class HabilidadeService {
    private final HabilidadeRepository habilidadeRepository;

    public HabilidadeService(HabilidadeRepository habilidadeRepository) {
        this.habilidadeRepository = habilidadeRepository;
    }

    public Habilidade criar(Habilidade habilidade) {
        if (habilidade.getNome() == null || habilidade.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        return habilidadeRepository.save(habilidade);
    }

    public Habilidade buscarPorId(Long id) {
        return habilidadeRepository.findById(id);
    }

    public Habilidade buscarPorNome(String nome) {
        return habilidadeRepository.findByNome(nome);
    }

    public List<Habilidade> listarTodas() {
        return habilidadeRepository.findAll();
    }

    public Habilidade atualizar(Long id, Habilidade dadosNovos) {
        Habilidade existente = habilidadeRepository.findById(id);
        if (existente == null) return null;
        existente.setNome(dadosNovos.getNome());
        existente.setDescricao(dadosNovos.getDescricao());
        habilidadeRepository.update(existente);
        return existente;
    }

    public boolean deletar(Long id) {
        return habilidadeRepository.delete(id);
    }
}

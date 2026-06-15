package service;

import model.Troca;
import repository.TrocaRepository;

import java.util.List;

public class TrocaService {
    private final TrocaRepository trocaRepository;

    public TrocaService(TrocaRepository trocaRepository) {
        this.trocaRepository = trocaRepository;
    }

    public Troca solicitar(Troca troca) {
        if (troca.getSolicitante() == null || troca.getDestinatario() == null) {
            throw new IllegalArgumentException("Solicitante e destinatário são obrigatórios.");
        }
        troca.setStatus("PENDENTE");
        return trocaRepository.save(troca);
    }

    public Troca buscarPorId(Long id) {
        return trocaRepository.findById(id);
    }

    public List<Troca> listarTodas() {
        return trocaRepository.findAll();
    }

    public Troca atualizarStatus(Long id, String novoStatus) {
        Troca troca = trocaRepository.findById(id);
        if (troca == null) return null;
        trocaRepository.updateStatus(id, novoStatus);
        troca.setStatus(novoStatus);
        return troca;
    }

    public boolean deletar(Long id) {
        return trocaRepository.delete(id);
    }
}

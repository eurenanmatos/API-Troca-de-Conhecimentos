package controller;

import model.Habilidade;
import service.HabilidadeService;

import java.util.List;

public class HabilidadeController {
    private final HabilidadeService habilidadeService;

    public HabilidadeController(HabilidadeService habilidadeService) {
        this.habilidadeService = habilidadeService;
    }

    public Habilidade criarHabilidade(Habilidade habilidade) {
        return habilidadeService.criar(habilidade);
    }

    public Habilidade buscarPorId(Long id) {
        return habilidadeService.buscarPorId(id);
    }

    public Habilidade buscarPorNome(String nome) {
        return habilidadeService.buscarPorNome(nome);
    }

    public List<Habilidade> listarHabilidades() {
        return habilidadeService.listarTodas();
    }

    public Habilidade atualizarHabilidade(Long id, Habilidade dadosNovos) {
        return habilidadeService.atualizar(id, dadosNovos);
    }

    public boolean deletarHabilidade(Long id) {
        return habilidadeService.deletar(id);
    }
}

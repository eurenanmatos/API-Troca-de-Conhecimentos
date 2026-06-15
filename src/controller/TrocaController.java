package controller;

import model.Troca;
import service.TrocaService;

import java.util.List;

public class TrocaController {
    private final TrocaService trocaService;

    public TrocaController(TrocaService trocaService) {
        this.trocaService = trocaService;
    }

    public Troca solicitarTroca(Troca troca) {
        return trocaService.solicitar(troca);
    }

    public Troca buscarPorId(Long id) {
        return trocaService.buscarPorId(id);
    }

    public List<Troca> listarTrocas() {
        return trocaService.listarTodas();
    }

    public Troca atualizarStatus(Long id, String status) {
        return trocaService.atualizarStatus(id, status);
    }

    public boolean deletarTroca(Long id) {
        return trocaService.deletar(id);
    }
}

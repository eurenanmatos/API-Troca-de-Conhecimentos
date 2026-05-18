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
        return trocaService.realizarTroca(troca);
    }

    public List<Troca> listarTrocas() {
        return trocaService.listarTrocas();
    }
}

package repository;

import model.Troca;
import java.util.ArrayList;
import java.util.List;

public class TrocaRepository {
    private final List<Troca> trocas = new ArrayList<>();

    public Troca save(Troca troca) {
        trocas.add(troca);
        return troca;
    }

    public Troca findById(Long id) {
        return trocas.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Troca> findAll() {
        return new ArrayList<>(trocas);
    }
}

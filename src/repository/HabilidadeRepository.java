package repository;

import model.Habilidade;
import java.util.ArrayList;
import java.util.List;

public class HabilidadeRepository {
    private final List<Habilidade> habilidades = new ArrayList<>();

    public Habilidade save(Habilidade habilidade) {
        habilidades.add(habilidade);
        return habilidade;
    }

    public Habilidade findById(Long id) {
        return habilidades.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Habilidade> findAll() {
        return new ArrayList<>(habilidades);
    }
}

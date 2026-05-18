package model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private Long id;
    private String nome;
    private Habilidade habilidadeOferecida;
    private Habilidade habilidadeDesejada;

    public Usuario() {
    }

    public Usuario(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Usuario(Long id, String nome, Habilidade habilidadeOferecida, Habilidade habilidadeDesejada) {
        this.id = id;
        this.nome = nome;
        this.habilidadeOferecida = habilidadeOferecida;
        this.habilidadeDesejada = habilidadeDesejada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Habilidade getHabilidadeOferecida() {
        return habilidadeOferecida;
    }

    public void setHabilidadeOferecida(Habilidade habilidadeOferecida) {
        this.habilidadeOferecida = habilidadeOferecida;
    }

    public Habilidade getHabilidadeDesejada() {
        return habilidadeDesejada;
    }

    public void setHabilidadeDesejada(Habilidade habilidadeDesejada) {
        this.habilidadeDesejada = habilidadeDesejada;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + '\'' + ", oferece=" + (habilidadeOferecida != null ? habilidadeOferecida.getNome() : "-") + ", deseja=" + (habilidadeDesejada != null ? habilidadeDesejada.getNome() : "-") + '}';
    }
}

package model;

public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private Habilidade habilidadeOferecida;
    private Habilidade habilidadeDesejada;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(Long id, String nome, String email, String senha,
                   Habilidade habilidadeOferecida, Habilidade habilidadeDesejada) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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
        return "Usuario{id=" + id + ", nome='" + nome + "', email='" + email + "'}";
    }
}

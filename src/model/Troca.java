package model;

public class Troca {
    private Long id;
    private Usuario solicitante;
    private Usuario destinatario;
    private Habilidade habilidadeOferecida;
    private Habilidade habilidadeDesejada;
    private String status;

    public Troca() {
    }

    public Troca(Long id, Usuario solicitante, Usuario destinatario, Habilidade habilidadeOferecida, Habilidade habilidadeDesejada) {
        this.id = id;
        this.solicitante = solicitante;
        this.destinatario = destinatario;
        this.habilidadeOferecida = habilidadeOferecida;
        this.habilidadeDesejada = habilidadeDesejada;
        this.status = "PENDENTE";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Usuario destinatario) {
        this.destinatario = destinatario;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Troca{id=" + id + ", solicitante=" + solicitante.getNome() + ", destinatario=" + destinatario.getNome() + ", habilidadeOferecida=" + habilidadeOferecida.getNome() + ", habilidadeDesejada=" + habilidadeDesejada.getNome() + ", status='" + status + '\'' + '}';
    }
}

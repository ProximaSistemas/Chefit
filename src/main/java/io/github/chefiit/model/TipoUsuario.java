package io.github.chefiit.model;

public enum TipoUsuario {
    USER("Usuário"),
    NUTRICIONISTA("Nutricionista"),
    NUTROLOGO("Nutrologo"),
    ADMIN("Administrador");
    
    private final String descricao;
    
    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}
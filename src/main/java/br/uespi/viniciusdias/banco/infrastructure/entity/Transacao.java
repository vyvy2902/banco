package br.uespi.viniciusdias.banco.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private BigDecimal valor;
    @Column(nullable = false, length = 100)
    private String descricao;
    @ManyToMany
    @JoinTable(
            name = "transacao_conta",
            joinColumns = @JoinColumn(name = "transacao_id"),
            inverseJoinColumns = @JoinColumn(name = "conta_id")
    )
    private List<Conta> contas;

    public Transacao(BigDecimal valor, String descricao, List<Conta> contas) {
        this.valor = valor;
        this.descricao = descricao;
        this.contas = contas;
    }
}

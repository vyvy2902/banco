package br.uespi.viniciusdias.banco.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter

@Entity
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String numeroConta;
    @Column(nullable = false)
    private BigDecimal saldo;
    @ManyToMany(mappedBy = "contas")
    private List<Pessoa> donosConta;

    public Conta(Long id, String numeroConta, List<Pessoa> donosConta) {
        this.id = id;
        this.numeroConta = numeroConta;
        this.donosConta = donosConta;
        saldo = BigDecimal.valueOf(0);
    }
}

package br.uespi.viniciusdias.banco.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valor;
    private LocalDate data;
    @Column(name = "taxa_juros")
    private static final BigDecimal TAXA_JUROS = BigDecimal.valueOf(0.05);
    @ManyToOne
    private Conta conta;

    public Emprestimo(BigDecimal valor, LocalDate data, Conta conta) {
        this.valor = valor;
        this.data = data;
        this.conta = conta;
    }

    public BigDecimal calcularJuros(int meses) {
        return valor.multiply(TAXA_JUROS).multiply(BigDecimal.valueOf(meses));
    }
}

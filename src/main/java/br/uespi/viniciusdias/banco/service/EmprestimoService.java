package br.uespi.viniciusdias.banco.service;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Emprestimo;
import br.uespi.viniciusdias.banco.infrastructure.repository.ContaRepository;
import br.uespi.viniciusdias.banco.infrastructure.repository.EmprestimoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmprestimoService {
    @Autowired
    private EmprestimoRepository emprestimoRepository;
    @Autowired
    private ContaRepository contaRepository;

    public List<Emprestimo> buscarEmprestimoPorConta(Conta conta) {
        return emprestimoRepository.findByConta(conta);
    }

    public Optional<Emprestimo> buscarPorId(Long id) {
        return emprestimoRepository.findById(id);
    }

    @Transactional
    public Emprestimo solicitarEmprestimo(Conta conta, BigDecimal valor) {
        Emprestimo emprestimo = new Emprestimo(valor, LocalDate.now(), conta);

        conta.setSaldo(conta.getSaldo().add(valor));

        System.out.println("Empréstimo realizado com sucesso, juros de 5% ao mês");
        emprestimoRepository.save(emprestimo);
        contaRepository.save(conta);

        return emprestimo;
    }

    @Transactional
    public void pagarEmprestimo(Long emprestimoId, Conta conta, BigDecimal valorPagamento) {
        if (valorPagamento.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do pagamento deve ser positivo.");
        }

        Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(emprestimoId);
        if (emprestimoOpt.isEmpty()) {
            throw new IllegalArgumentException("Empréstimo não encontrado.");
        }

        Emprestimo emprestimo = emprestimoOpt.get();

        if (conta.getSaldo().compareTo(valorPagamento) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para o pagamento.");
        }

        BigDecimal saldoDevedor = emprestimo.getValor().subtract(emprestimo.getValorPago());
        if (valorPagamento.compareTo(saldoDevedor) > 0) {
            throw new IllegalArgumentException("O valor do pagamento não pode exceder o saldo devedor.");
        }

        emprestimo.setValorPago(emprestimo.getValorPago().add(valorPagamento));
        conta.setSaldo(conta.getSaldo().subtract(valorPagamento));

        contaRepository.save(conta);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void cobrarJuros() {
        List<Emprestimo> emprestimos = emprestimoRepository.findAll();
        for (Emprestimo emprestimo : emprestimos) {
            Conta conta = emprestimo.getConta();
            BigDecimal juros = emprestimo.calcularJuros(1); // 1 mês
            conta.setSaldo(conta.getSaldo().subtract(juros)); // Deduz juros do saldo

            contaRepository.save(conta);
        }
    }

    public void deletarEmprestimo(Long id) {
        emprestimoRepository.deleteById(id);
    }
}

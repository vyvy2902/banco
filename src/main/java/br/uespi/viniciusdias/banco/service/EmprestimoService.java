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
    @Transactional
    public Emprestimo solicitarEmprestimo(Long contaId, BigDecimal valor) {
        Optional<Conta> contaOpt = contaRepository.findById(contaId);
        if (contaOpt.isEmpty()) {
            throw new IllegalArgumentException("Conta não encontrada.");
        }

        Conta conta = contaOpt.get();

        Emprestimo emprestimo = new Emprestimo(valor, LocalDate.now(), conta);

        conta.setSaldo(conta.getSaldo().add(valor));

        emprestimoRepository.save(emprestimo);
        contaRepository.save(conta);

        return emprestimo;
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
}

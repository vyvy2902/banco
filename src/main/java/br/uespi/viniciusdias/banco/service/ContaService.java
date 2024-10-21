package br.uespi.viniciusdias.banco.service;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Pessoa;
import br.uespi.viniciusdias.banco.infrastructure.repository.ContaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    public Conta salvar(Conta conta) {
        return contaRepository.save(conta);
    }

    public Optional<Conta> buscarPorId(Long id) {
        return contaRepository.findById(id);
    }

    public List<Conta> buscarTodas() {
        return contaRepository.findAll();
    }

    public void deletar(Long id) {
        contaRepository.deleteById(id);
    }

    public Optional<Conta> buscarPorNumeroConta(String numeroConta) {
        return contaRepository.findByNumeroConta(numeroConta);
    }

    public List<Conta> buscarContasPorPessoa(Pessoa pessoa) {
        return contaRepository.findByDonosConta(pessoa);
    }

    @Transactional
    public Conta depositar(Long id, BigDecimal valor) {
        Optional<Conta> contaOpt = buscarPorId(id);

        if (contaOpt.isPresent() && valor.compareTo(BigDecimal.ZERO) > 0) {
            Conta conta = contaOpt.get();
            conta.setSaldo(conta.getSaldo().add(valor));
            salvar(conta);
            return conta;
        }else {
            throw new IllegalArgumentException("Valor de depósito deve ser maior que zero ou conta não encontrada.");
        }

    }

    @Transactional
    public Conta sacar(Long id, BigDecimal valor) {
        Optional<Conta> contaOpt = buscarPorId(id);
        if (contaOpt.isPresent()) {
            Conta conta = contaOpt.get();
            if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(conta.getSaldo()) <= 0) {
                conta.setSaldo(conta.getSaldo().subtract(valor));
                salvar(conta);
                return conta;
            } else {
                throw new IllegalArgumentException("Saque deve ser maior que zero e menor ou igual ao saldo.");
            }
        } else {
            throw new IllegalArgumentException("Conta não encontrada.");
        }

    }

    public String gerarNumeroConta() {
        SecureRandom random = new SecureRandom();
        StringBuilder numeroConta = new StringBuilder();

        for (int i = 0; i < 20; i++) {
            int digito = random.nextInt(10);
            numeroConta.append(digito);
        }

        Optional<Conta> conta = buscarPorNumeroConta(numeroConta.toString());
        if (conta.isPresent()) {
            gerarNumeroConta();
        }
        return numeroConta.toString();
    }

}

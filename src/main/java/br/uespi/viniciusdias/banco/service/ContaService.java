package br.uespi.viniciusdias.banco.service;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.repository.ContaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Transactional
    public void depositar(Long id, BigDecimal valor) {
        Optional<Conta> contaOpt = buscarPorId(id);

        if (contaOpt.isPresent() && valor.compareTo(BigDecimal.ZERO) > 0) {
            Conta conta = contaOpt.get();
            conta.setSaldo(conta.getSaldo().add(valor));
            salvar(conta);
        }else {
            throw new IllegalArgumentException("Valor de depósito deve ser maior que zero ou conta não encontrada.");
        }

    }

    @Transactional
    public void sacar(Long id, BigDecimal valor) {
        Optional<Conta> contaOpt = buscarPorId(id);
        if (contaOpt.isPresent()) {
            Conta conta = contaOpt.get();
            if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(conta.getSaldo()) <= 0) {
                conta.setSaldo(conta.getSaldo().subtract(valor));
                salvar(conta);
            } else {
                throw new IllegalArgumentException("Saque deve ser maior que zero e menor ou igual ao saldo.");
            }
        } else {
            throw new IllegalArgumentException("Conta não encontrada.");
        }
    }

}

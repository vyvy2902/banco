package br.uespi.viniciusdias.banco.service;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Transacao;
import br.uespi.viniciusdias.banco.infrastructure.repository.ContaRepository;
import br.uespi.viniciusdias.banco.infrastructure.repository.TransacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {
    @Autowired
    private TransacaoRepository transacaoRepository;
    @Autowired
    private ContaRepository contaRepository;

    @Transactional
    public Transacao realizarTransacao(Conta contaOrigem, Conta contaDestino, BigDecimal valor, String descricao) {
        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente na conta de origem.");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        List<Conta> contas = new ArrayList<>();
        contas.add(contaOrigem);
        contas.add(contaDestino);
        Transacao transacao = new Transacao(valor, descricao, contas);
        transacaoRepository.save(transacao);
        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        return transacao;
    }

    public List<Transacao> buscarTransacoesPorConta(Conta conta) {
        return transacaoRepository.findByContas(conta);
    }
}

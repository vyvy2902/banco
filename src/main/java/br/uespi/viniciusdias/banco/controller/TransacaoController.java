package br.uespi.viniciusdias.banco.controller;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Transacao;
import br.uespi.viniciusdias.banco.service.ContaService;
import br.uespi.viniciusdias.banco.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class TransacaoController {

    @Autowired
    TransacaoService transacaoService;
    @Autowired
    ContaService contaService;
    Scanner scanner;

    public TransacaoController(@Autowired TransacaoService transacaoService, @Autowired ContaService contaService) {
        scanner = new Scanner(System.in);
        this.transacaoService = transacaoService;
        this.contaService = contaService;
    }

    public Conta efetuarTransacao(Conta conta) {
        System.out.println("Número da conta ao qual você deseja efetuar uma transação: ");
        String numeroConta = scanner.nextLine();
        Optional<Conta> contaOPT = contaService.buscarPorNumeroConta(numeroConta);
        if (contaOPT.isPresent()) {
            Conta contaTMP = contaOPT.get();
            System.out.println("Valor a ser enviado para a conta destino");
            String valorTransacao = scanner.nextLine();
            System.out.println("Descrição da transação");
            String descricaoTransacao = scanner.nextLine();
            transacaoService.realizarTransacao(conta, contaTMP, new BigDecimal(valorTransacao), descricaoTransacao);
        }else {
            System.out.println("Conta inexistente");
        }
        return conta;
    }

    public Conta verHistoricoTransacoes(Conta conta) {
        List<Transacao> transacoes = transacaoService.buscarTransacoesPorConta(conta);
        if (transacoes.isEmpty()) {
            System.out.println("Nenhuma transação encontrada");
        }else {
            for (Transacao transacao : transacoes) {
                List<Conta> contas = transacao.getContas();
                Conta contaOrigem = contas.getFirst();
                Conta contaDestino = contas.getLast();
                System.out.println("Descrição: " + transacao.getDescricao());
                System.out.println("Valor da transação: " + transacao.getValor());
                System.out.println("Conta origem: " + contaOrigem.getNumeroConta());
                System.out.println("Conta destino: " + contaDestino.getNumeroConta());
                System.out.println();
            }
        }
        return conta;
    }
}

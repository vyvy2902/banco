package br.uespi.viniciusdias.banco.controller;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class ContaController {
    @Autowired
    ContaService contaService;
    Scanner scanner;

    public ContaController(@Autowired ContaService contaService) {
        scanner = new Scanner(System.in);
        this.contaService = contaService;
    }

    public Conta depositar(Conta conta) {
        System.out.println("Valor a ser depositado:");
        String valorDepositado = scanner.nextLine();
        conta = contaService.depositar(conta.getId(), new BigDecimal(valorDepositado));
        return conta;
    }

    public Conta sacar(Conta conta) {
        System.out.println("Valor a ser sacado: ");
        String valorSacado = scanner.nextLine();
        conta = contaService.sacar(conta.getId(), new BigDecimal(valorSacado));
        return conta;
    }

    public void cancelar(Conta conta) {
        contaService.deletar(conta.getId());
        System.out.println("Conta deletada com sucesso");
    }
}

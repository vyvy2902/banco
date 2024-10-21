package br.uespi.viniciusdias.banco.controller;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Emprestimo;
import br.uespi.viniciusdias.banco.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class EmprestimoController {

    @Autowired
    EmprestimoService emprestimoService;
    Scanner scanner;
    public EmprestimoController (@Autowired EmprestimoService emprestimoService) {
        scanner = new Scanner(System.in);
        this.emprestimoService = emprestimoService;
    }

    public Conta pedirEmprestimo(Conta conta) {
        System.out.println("Qual o valor do empréstimo?");
        Emprestimo emprestimo = emprestimoService.solicitarEmprestimo(conta, new BigDecimal(scanner.nextLine()));
        return conta;
    }

    public Conta pagarEmprestimo(Conta conta) {
        List<Emprestimo> emprestimos = emprestimoService.buscarEmprestimoPorConta(conta);
        if (emprestimos.isEmpty()) {
            System.out.println("Nenhum emprestimo encontrado");
        }else {
            for (Emprestimo emprestimoTmp : emprestimos) {
                System.out.println(emprestimoTmp.getId() + " - "  + emprestimoTmp.getValor() + " - " + emprestimoTmp.getValorPago());
            }
            System.out.println("Selecione o id do empréstimo que será pago");
            long idEmprestimo = scanner.nextLong();
            scanner.nextLine();
            Optional<Emprestimo> emprestimoOptional = emprestimoService.buscarPorId(idEmprestimo);
            Emprestimo emprestimoPago;
            if (emprestimoOptional.isPresent()) {
                emprestimoPago = emprestimoOptional.get();
                System.out.println("Quanto você irá pagar?");
                BigDecimal valorPagoEmprestimo = new BigDecimal(scanner.nextLine());
                emprestimoService.pagarEmprestimo(emprestimoPago.getId(), conta, valorPagoEmprestimo);
                if (emprestimoPago.getValor().compareTo(valorPagoEmprestimo) == 0) {
                    emprestimoService.deletarEmprestimo(emprestimoPago.getId());
                    System.out.println("Foi pago se pá");
                }
            }else {
                System.out.println("Id inválido");
            }
        }

        return conta;
    }
}

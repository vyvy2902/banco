package br.uespi.viniciusdias.banco.controller;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Pessoa;
import br.uespi.viniciusdias.banco.service.ContaService;
import br.uespi.viniciusdias.banco.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class ContaController {
    @Autowired
    ContaService contaService;
    @Autowired
    PessoaController pessoaController;
    @Autowired
    PessoaService pessoaService;
    @Autowired
    TransacaoController transacaoController;
    @Autowired
    EmprestimoController emprestimoController;
    Scanner scanner;
    private static final String contaBancaria = """
			
			   _____            _          ____                             _      \s
			  / ____|          | |        |  _ \\                           (_)     \s
			 | |     ___  _ __ | |_ __ _  | |_) | __ _ _ __   ___ __ _ _ __ _  __ _\s
			 | |    / _ \\| '_ \\| __/ _` | |  _ < / _` | '_ \\ / __/ _` | '__| |/ _` |
			 | |___| (_) | | | | || (_| | | |_) | (_| | | | | (_| (_| | |  | | (_| |
			  \\_____\\___/|_| |_|\\__\\__,_| |____/ \\__,_|_| |_|\\___\\__,_|_|  |_|\\__,_|
			                                                                       \s
			                                                                       \s
			""";

    public ContaController(@Autowired ContaService contaService, @Autowired PessoaController pessoaController, @Autowired PessoaService pessoaService, @Autowired TransacaoController transacaoController, @Autowired EmprestimoController emprestimoController) {
        scanner = new Scanner(System.in);
        this.contaService = contaService;
        this.pessoaController = pessoaController;
        this.pessoaService = pessoaService;
        this.transacaoController = transacaoController;
        this.emprestimoController = emprestimoController;
    }



    public void criarConta() {
        System.out.println("Quantas pessoas serão donas dessa conta?");
        int numeroPessoas = scanner.nextInt();
        scanner.nextLine();
        ArrayList donosConta = pessoaController.criarPessoas(numeroPessoas);
        Conta conta = new Conta(contaService.gerarNumeroConta(), donosConta);
        contaService.salvar(conta);
    }

    public Pessoa acessarConta() {
        Boolean contasDisponiveis = true;
        Optional<Pessoa> pessoa;
        while (true) {
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();
            pessoa = pessoaService.autenticar(email, senha);

            if (pessoa.isPresent()) {
                return pessoa.get();
            }else {
                System.out.println("Email ou senha inválidos");
            }
        }

    }

    public boolean mostrarContasDisponiveis(Pessoa pessoa) {
        List<Conta> contas = contaService.buscarContasPorPessoa(pessoa);
        System.out.println("Lista de contas");
        int contador = 1;
        if (contas.isEmpty()) {
            System.out.println("Nenhuma conta encontrada para essa pessoa");
            return false;
        }else {
            for (Conta conta : contas) {
                System.out.println(contador + "-" + conta.getNumeroConta());
                contador++;
            }
        }
        return true;

    }

    public int selecionarConta() {
        System.out.println("Qual conta você deseja acessar?");
        int contaEscolhida = scanner.nextInt();
        scanner.nextLine();
        return contaEscolhida - 1;
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

    public void acessarMenuConta(Conta conta) {

        boolean continuarLoop = true;

        while(continuarLoop) {
            System.out.println(contaBancaria);
            System.out.println("Número da conta: " + conta.getNumeroConta());
            System.out.println("Saldo: " + conta.getSaldo());
            System.out.println("1 - Depositar");
            System.out.println("2 - Sacar");
            System.out.println("3 - Cancelar conta");
            System.out.println("4 - Efetuar transação");
            System.out.println("5 - Pedir empréstimo");
            System.out.println("6 - Pagar empréstimo");
            System.out.println("7 - Histórico de transações");
            System.out.println("8 - Sair");
            int escolha = scanner.nextInt();
            scanner.nextLine();
            switch (escolha) {
                case 1:
                    conta = depositar(conta);
                    break;
                case 2:
                    conta = sacar(conta);
                    break;
                case 3:
                    continuarLoop = false;
                    cancelar(conta);
                    break;
                case 4:
                    conta = transacaoController.efetuarTransacao(conta);
                    break;
                case 5:
                    conta = emprestimoController.pedirEmprestimo(conta);
                    break;
                case 6:
                    conta = emprestimoController.pagarEmprestimo(conta);
                    break;
                case 7:
                    conta = transacaoController.verHistoricoTransacoes(conta);
                    break;
                case 8:
                    System.out.println("Adeus!");
                    continuarLoop = false;
                    break;
                default:
                    System.out.print("Valor inválido");
            }
        }
    }
}

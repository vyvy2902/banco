package br.uespi.viniciusdias.banco;

import br.uespi.viniciusdias.banco.controller.ContaController;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Pessoa;
import br.uespi.viniciusdias.banco.service.ContaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


import java.util.List;
import java.util.Scanner;

@SpringBootApplication
@EnableScheduling
public class BancoApplication implements CommandLineRunner {
	Scanner scanner = new Scanner(System.in);
	@Autowired
	private ContaService contaService;
	@Autowired
	private ContaController contaController;

	private final String logo = """
            
             .----------------.  .----------------.  .-----------------. .----------------.  .----------------.\s
            | .--------------. || .--------------. || .--------------. || .--------------. || .--------------. |
            | |   ______     | || |      __      | || | ____  _____  | || |     ______   | || |     ____     | |
            | |  |_   _ \\    | || |     /  \\     | || ||_   \\|_   _| | || |   .' ___  |  | || |   .'    `.   | |
            | |    | |_) |   | || |    / /\\ \\    | || |  |   \\ | |   | || |  / .'   \\_|  | || |  /  .--.  \\  | |
            | |    |  __'.   | || |   / ____ \\   | || |  | |\\ \\| |   | || |  | |         | || |  | |    | |  | |
            | |   _| |__) |  | || | _/ /    \\ \\_ | || | _| |_\\   |_  | || |  \\ `.___.'\\  | || |  \\  `--'  /  | |
            | |  |_______/   | || ||____|  |____|| || ||_____|\\____| | || |   `._____.'  | || |   `.____.'   | |
            | |              | || |              | || |              | || |              | || |              | |
            | '--------------' || '--------------' || '--------------' || '--------------' || '--------------' |
             '----------------'  '----------------'  '----------------'  '----------------'  '----------------'\s
            """;


	public static void main(String[] args) {
		SpringApplication.run(BancoApplication.class, args);
	}


	public void run(String... args) throws Exception {
		inicializar();
	}

	public void inicializar() {

		int opcao;

		do {
			System.out.println(logo);
			System.out.println();
			System.out.println("1 - Criar conta");
			System.out.println("2 - Acessar conta");
			opcao = scanner.nextInt();
			scanner.nextLine();
			switch (opcao) {
				case 1:
					contaController.criarConta();
					inicializar();
					break;
				case 2:
					Pessoa pessoa = contaController.acessarConta();
					boolean contaEncontrada = contaController.mostrarContasDisponiveis(pessoa);
					if (!contaEncontrada) {
						inicializar();
					}else {
						List<Conta> contas = contaService.buscarContasPorPessoa(pessoa);
						int contaEscolhida = contaController.selecionarConta();
						contaController.acessarMenuConta(contas.get(contaEscolhida));
						inicializar();
					}
					break;
				default:
					System.out.println("Opção inválida");
			}
		}while (opcao != 1 & opcao != 2);

	}
}

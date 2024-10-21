package br.uespi.viniciusdias.banco;

import br.uespi.viniciusdias.banco.controller.ContaController;
import br.uespi.viniciusdias.banco.controller.EmprestimoController;
import br.uespi.viniciusdias.banco.controller.TransacaoController;
import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Pessoa;
import br.uespi.viniciusdias.banco.service.ContaService;
import br.uespi.viniciusdias.banco.service.EmprestimoService;
import br.uespi.viniciusdias.banco.service.PessoaService;
import br.uespi.viniciusdias.banco.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
@EnableScheduling
public class BancoApplication implements CommandLineRunner {
	Scanner scanner = new Scanner(System.in);
	@Autowired
	private PessoaService pessoaService;
	@Autowired
	private ContaService contaService;
	@Autowired
	private TransacaoService transacaoService;
	@Autowired
	private EmprestimoService emprestimoService;
	@Autowired
	private ContaController contaController;
	@Autowired
	private TransacaoController transacaoController;
	@Autowired
	EmprestimoController emprestimoController;

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
	private final String dadosPessoa = """
			
			 _____            _                 _                                        \s
			|  __ \\          | |               | |                                       \s
			| |  | | __ _  __| | ___  ___    __| | __ _   _ __   ___  ___ ___  ___   __ _\s
			| |  | |/ _` |/ _` |/ _ \\/ __|  / _` |/ _` | | '_ \\ / _ \\/ __/ __|/ _ \\ / _` |
			| |__| | (_| | (_| | (_) \\__ \\ | (_| | (_| | | |_) |  __/\\__ \\__ \\ (_) | (_| |
			|_____/ \\__,_|\\__,_|\\___/|___/  \\__,_|\\__,_| | .__/ \\___||___/___/\\___/ \\__,_|
			                                             | |                             \s
			                                             |_|                             \s
			
            """;
	private final String contaBancaria = """
			
			   _____            _          ____                             _      \s
			  / ____|          | |        |  _ \\                           (_)     \s
			 | |     ___  _ __ | |_ __ _  | |_) | __ _ _ __   ___ __ _ _ __ _  __ _\s
			 | |    / _ \\| '_ \\| __/ _` | |  _ < / _` | '_ \\ / __/ _` | '__| |/ _` |
			 | |___| (_) | | | | || (_| | | |_) | (_| | | | | (_| (_| | |  | | (_| |
			  \\_____\\___/|_| |_|\\__\\__,_| |____/ \\__,_|_| |_|\\___\\__,_|_|  |_|\\__,_|
			                                                                       \s
			                                                                       \s
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
					criarConta();
					break;
				case 2:
					acessarConta();
					break;
				default:
					System.out.println("Opção inválida");
			}
		}while (opcao != 1 & opcao != 2);

	}

	private void criarConta() {
		System.out.println("Quantas pessoas serão donas dessa conta?");
		int numeroPessoas = scanner.nextInt();
		scanner.nextLine();
		ArrayList donosConta = criarPessoas(numeroPessoas);
		Conta conta = new Conta(contaService.gerarNumeroConta(), donosConta);
		contaService.salvar(conta);
		inicializar();
	}

	private void acessarConta() {
		Optional<Pessoa> pessoa;
		while (true) {
			System.out.print("Email: ");
			String email = scanner.nextLine();
			System.out.print("Senha: ");
			String senha = scanner.nextLine();
			pessoa = pessoaService.autenticar(email, senha);

			if (pessoa.isPresent()) {
				break;
			}else {
				System.out.println("");
			}
		}

		mostrarContasDisponiveis(pessoa.get());

	}

	public void mostrarContasDisponiveis(Pessoa pessoa) {
		List<Conta> contas = contaService.buscarContasPorPessoa(pessoa);
		System.out.println("Lista de contas");
		int contador = 1;
		if (contas.isEmpty()) {
			System.out.println("Nenhuma conta encontrada para essa pessoa");
			inicializar();
		}else {
			for (Conta conta : contas) {
				System.out.println(contador + "-" + conta.getNumeroConta());
				contador++;
			}
		}

		System.out.println("Qual conta você deseja acessar?");
		int contaEscolhida = scanner.nextInt();
		scanner.nextLine();
		acessarConta(contas.get(contaEscolhida - 1));
	}



	public void acessarConta(Conta conta) {

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
					conta = contaController.depositar(conta);
					break;
				case 2:
					conta = contaController.sacar(conta);
					break;
				case 3:
					continuarLoop = false;
					contaController.cancelar(conta);
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

		inicializar();
	}



	private Pessoa criarPessoa() {
		System.out.println(dadosPessoa);
		System.out.print("Nome: ");
		String nome = scanner.nextLine();
		System.out.print("CPF: ");
		String cpf = scanner.nextLine();
		System.out.print("Email: ");
		String email = scanner.nextLine();
		System.out.print("Senha: ");
		String senha = scanner.nextLine();
		Pessoa pessoa = new Pessoa(nome, cpf, email, senha);
		return pessoaService.salvar(pessoa);
	}

	private ArrayList<Pessoa> criarPessoas(int numeroPessoas) {

		ArrayList<Pessoa> listaPessoas = new ArrayList<>();

		for (int i = 0; i < numeroPessoas; i++) {
			listaPessoas.add(criarPessoa());
		}

		return listaPessoas;
	}


}

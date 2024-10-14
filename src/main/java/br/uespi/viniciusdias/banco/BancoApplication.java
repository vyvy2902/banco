package br.uespi.viniciusdias.banco;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Pessoa;
import br.uespi.viniciusdias.banco.service.ContaService;
import br.uespi.viniciusdias.banco.service.PessoaService;
import br.uespi.viniciusdias.banco.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;
import java.security.SecureRandom;
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
             _                          _               \s
            | \\ _  _| _  _     _| _    |_) _  _  _  _  _\s
            |_/(_|(_|(_)_>    (_|(_|   |  (/__> _> (_)(_|
            
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
				case 3:
					System.out.println("Opção inválida");
			}
		}while (opcao != 1 & opcao != 2);

	}

	private void criarConta() {
		System.out.println("Quantas pessoas serão donas dessa conta?");
		int numeroPessoas = scanner.nextInt();
		scanner.nextLine();
		ArrayList donosConta = criarPessoas(numeroPessoas);
		Conta conta = new Conta(gerarNumeroConta(), donosConta);
		contaService.salvar(conta);
		inicializar();
	}

	private void acessarConta() {
		Optional<Pessoa> pessoa;
		while (true) {
			System.out.print("Email:");
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
		for (Conta conta : contas) {
			System.out.println(contador + "-" + conta.getNumeroConta());
			contador++;
		}
		System.out.println("Qual conta você deseja acessar?");
		int contaEscolhida = scanner.nextInt();
		scanner.nextLine();
		acessarConta(contas.get(contaEscolhida - 1));
	}

	public void acessarConta(Conta conta) {

		boolean continuarLoop = true;

		while(continuarLoop) {
			System.out.println("Número da conta: " + conta.getNumeroConta());
			System.out.println("Saldo: " + conta.getSaldo());
			System.out.println("1 - Depositar");
			System.out.println("2 - Sacar");
			System.out.println("3 - Cancelar conta");
			System.out.println("4 - Efetuar transação");
			System.out.println("5 - Sair");
			int escolha = scanner.nextInt();
			scanner.nextLine();
			switch (escolha) {
				case 1:
					System.out.println("Valor a ser depositado:");
					String valorDepositado = scanner.nextLine();
					conta = contaService.depositar(conta.getId(), new BigDecimal(valorDepositado));
					break;
				case 2:
					System.out.println("Valor a ser sacado: ");
					String valorSacado = scanner.nextLine();
					conta = contaService.sacar(conta.getId(), new BigDecimal(valorSacado));
					break;
				case 3:
					continuarLoop = false;
					contaService.deletar(conta.getId());
					System.out.println("Conta deletada com sucesso");
					break;
				case 4:
					System.out.println("Número da conta ao qual você deseja efetuar uma transação: ");
					String numeroConta = scanner.nextLine();
					Optional<Conta> contaOPT = contaService.buscarPorNumeroConta(numeroConta);
					if (contaOPT.isPresent()) {
						Conta contaTMP = contaOPT.get();
						System.out.println("Valor a ser enviado para a conta destino");
						String valorTransacao = scanner.nextLine();
						System.out.println("Descrição da transação");
						String descricaoTransacao = scanner.nextLine();
						transacaoService.realizarTransacao(conta.getId(), contaTMP.getId(), new BigDecimal(valorTransacao), descricaoTransacao);
					}else {
						System.out.println("Conta inexistente");
					}
					break;
				case 5:
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

	private String gerarNumeroConta() {
		SecureRandom random = new SecureRandom();
		StringBuilder numeroConta = new StringBuilder();

		for (int i = 0; i < 20; i++) {
			int digito = random.nextInt(10);
			numeroConta.append(digito);
		}

		return numeroConta.toString();
	}
}

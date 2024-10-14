package br.uespi.viniciusdias.banco;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Pessoa;
import br.uespi.viniciusdias.banco.service.ContaService;
import br.uespi.viniciusdias.banco.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class BancoApplication implements CommandLineRunner {
	Scanner scanner = new Scanner(System.in);
	@Autowired
	private PessoaService pessoaService;
	@Autowired
	private ContaService contaService;
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
					System.out.println("Acessar conta");
					break;
				case 3:
					System.out.println("Opção inválida");
			}
		}while (opcao != 1 & opcao != 2);

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

	private void criarConta() {
		System.out.println("Quantas pessoas serão donas dessa conta?");
		int numeroPessoas = scanner.nextInt();
		scanner.nextLine();
		ArrayList donosConta = criarPessoas(numeroPessoas);
		Conta conta = new Conta(gerarNumeroConta(), donosConta);
		contaService.salvar(conta);
		inicializar();
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

package br.uespi.viniciusdias.banco;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Emprestimo;
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
	@Autowired
	private EmprestimoService emprestimoService;

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
			transacaoService.realizarTransacao(conta.getId(), contaTMP.getId(), new BigDecimal(valorTransacao), descricaoTransacao);
		}else {
			System.out.println("Conta inexistente");
		}
		return conta;
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

	public void acessarConta(Conta conta) {

		boolean continuarLoop = true;

		while(continuarLoop) {
			System.out.println("Número da conta: " + conta.getNumeroConta());
			System.out.println("Saldo: " + conta.getSaldo());
			System.out.println("1 - Depositar");
			System.out.println("2 - Sacar");
			System.out.println("3 - Cancelar conta");
			System.out.println("4 - Efetuar transação");
			System.out.println("5 - Pedir empréstimo");
			System.out.println("6 - Pagar empréstimo");
			System.out.println("7 - Sair");
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
					conta = efetuarTransacao(conta);
				case 5:
					conta = pedirEmprestimo(conta);
					break;
				case 6:
					conta = pagarEmprestimo(conta);
					break;
				case 7:
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

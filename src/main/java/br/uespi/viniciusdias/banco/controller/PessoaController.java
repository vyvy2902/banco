package br.uespi.viniciusdias.banco.controller;

import br.uespi.viniciusdias.banco.infrastructure.entity.Pessoa;
import br.uespi.viniciusdias.banco.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Scanner;

@Component
public class PessoaController {

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
    private Scanner scanner;
    @Autowired
    private PessoaService pessoaService;

    public PessoaController(@Autowired PessoaService pessoaService) {
        this.pessoaService = pessoaService;
        scanner = new Scanner(System.in);
    }

    public Pessoa criarPessoa() {
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

    public ArrayList<Pessoa> criarPessoas(int numeroPessoas) {

        ArrayList<Pessoa> listaPessoas = new ArrayList<>();

        for (int i = 0; i < numeroPessoas; i++) {
            listaPessoas.add(criarPessoa());
        }

        return listaPessoas;
    }
}

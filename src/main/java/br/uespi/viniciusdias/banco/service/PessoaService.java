package br.uespi.viniciusdias.banco.service;

import br.uespi.viniciusdias.banco.infrastructure.entity.Pessoa;
import br.uespi.viniciusdias.banco.infrastructure.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PessoaService {
    @Autowired
    private PessoaRepository pessoaRepository;

    public Pessoa salvar(Pessoa pessoa) {
        return pessoaRepository.save(pessoa);
    }

    public Optional<Pessoa> buscarPorId(Long id) {
        return pessoaRepository.findById(id);
    }

    public Optional<Pessoa> buscarPorCpf(String cpf) {
        return pessoaRepository.findByCpf(cpf);
    }

    public Optional<Pessoa> buscarPorEmail(String email) {
        return pessoaRepository.findByEmail(email);
    }

    public List<Pessoa> buscarTodas() {
        return pessoaRepository.findAll();
    }

    public void deletar(Long id) {
        pessoaRepository.deleteById(id);
    }

    public Pessoa atualizarNome(Long id, String novoNome) {
        return pessoaRepository.findById(id).map(pessoa -> {
            pessoa.setNome(novoNome);
            return pessoaRepository.save(pessoa);
        }).orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada para o ID: " + id));
    }

    public Pessoa atualizarCpf(Long id, String novoCpf) {
        return pessoaRepository.findById(id).map(pessoa -> {
            pessoa.setCpf(novoCpf);
            return pessoaRepository.save(pessoa);
        }).orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada para o ID: " + id));
    }

    public Pessoa atualizarEmail(Long id, String novoEmail) {
        return pessoaRepository.findById(id).map(pessoa -> {
            pessoa.setEmail(novoEmail);
            return pessoaRepository.save(pessoa);
        }).orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada para o ID: " + id));
    }

    public Pessoa atualizarPessoa(Long id, Pessoa novaPessoa) {
        return pessoaRepository.findById(id).map(pessoaExistente -> {
            pessoaExistente.setNome(novaPessoa.getNome());
            pessoaExistente.setCpf(novaPessoa.getCpf());
            pessoaExistente.setEmail(novaPessoa.getEmail());
            pessoaExistente.setSenha(novaPessoa.getSenha());
            pessoaExistente.setContas(novaPessoa.getContas());
            return pessoaRepository.save(pessoaExistente);
        }).orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada para o ID: " + id));
    }
}

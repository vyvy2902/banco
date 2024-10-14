package br.uespi.viniciusdias.banco.infrastructure.repository;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumeroConta(String numeroConta);
    List<Conta> findByDonosConta(Pessoa pessoa);
}

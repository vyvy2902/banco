package br.uespi.viniciusdias.banco.infrastructure.repository;

import br.uespi.viniciusdias.banco.infrastructure.entity.Conta;
import br.uespi.viniciusdias.banco.infrastructure.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByContas(Conta conta);
}

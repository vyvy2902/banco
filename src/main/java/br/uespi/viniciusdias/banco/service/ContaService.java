package br.uespi.viniciusdias.banco.service;

import br.uespi.viniciusdias.banco.infrastructure.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ContaService {

    @Autowired
    private ContaRepository contaRepository;
}

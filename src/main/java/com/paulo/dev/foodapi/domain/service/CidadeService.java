package com.paulo.dev.foodapi.domain.service;

import com.paulo.dev.foodapi.domain.exception.CidadeNaoEncontradaException;
import com.paulo.dev.foodapi.domain.exception.EntidadeNaoEncontradaException;
import com.paulo.dev.foodapi.domain.model.Cidade;
import com.paulo.dev.foodapi.domain.model.Estado;
import com.paulo.dev.foodapi.domain.repository.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CidadeService {

    @Autowired
    private CidadeRepository cidadeRepository;

    @Autowired
    private EstadoService estadoService;

    public List<Cidade> listar() {
        return cidadeRepository.findAll();
    }

    public Cidade buscar(Long id) {
        return cidadeRepository.findById(id).orElseThrow(() -> {
            throw new CidadeNaoEncontradaException(id);
        });
    }

    public Cidade cadastrar(Cidade cidade) {
        Estado estado = estadoService.buscar(cidade.getEstado().getId());
        cidade.setEstado(estado);
        return cidadeRepository.save(cidade);
    }

    public Cidade editar(Cidade cidade) {
        Estado estado = estadoService.buscar(cidade.getEstado().getId());
        cidade.setEstado(estado);
        return cidadeRepository.save(cidade);
    }

    public void remover(Long id) {
        try {
            cidadeRepository.deleteById(id);
        }  catch (EmptyResultDataAccessException ex) {
            throw new CidadeNaoEncontradaException(id);
        }
    }

}

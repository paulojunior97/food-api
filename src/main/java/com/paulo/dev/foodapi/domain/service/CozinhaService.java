package com.paulo.dev.foodapi.domain.service;

import com.paulo.dev.foodapi.domain.exception.CozinhaNaoEncontradaException;
import com.paulo.dev.foodapi.domain.exception.EntidadeEmUsoException;
import com.paulo.dev.foodapi.domain.exception.EntidadeNaoEncontradaException;
import com.paulo.dev.foodapi.domain.model.Cozinha;
import com.paulo.dev.foodapi.domain.repository.CozinhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CozinhaService {

    @Autowired
    private CozinhaRepository cozinhaRepository;

    public List<Cozinha> listar() {
        return cozinhaRepository.findAll();
    }

    public Cozinha buscar(Long id) {
        return cozinhaRepository.findById(id).orElseThrow(
                () -> {
                    throw new CozinhaNaoEncontradaException(id);
                });
    }

    public Cozinha cadastrar(Cozinha cozinha) {
        return cozinhaRepository.save(cozinha);
    }

    public Cozinha editar(Cozinha cozinha) {
        buscar(cozinha.getId());
        return cozinhaRepository.save(cozinha);
    }

    public void remover(Long id) {
        try {
            cozinhaRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new CozinhaNaoEncontradaException(id);
        } catch (DataIntegrityViolationException ex) {
            throw new EntidadeEmUsoException("Cozinha está sendo usada.");
        }
    }
}

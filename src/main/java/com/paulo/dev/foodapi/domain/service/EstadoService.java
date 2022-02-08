package com.paulo.dev.foodapi.domain.service;

import com.paulo.dev.foodapi.domain.exception.EntidadeEmUsoException;
import com.paulo.dev.foodapi.domain.exception.EstadoNaoEncontradoException;
import com.paulo.dev.foodapi.domain.model.Estado;
import com.paulo.dev.foodapi.domain.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoService {

    @Autowired
    private EstadoRepository estadoRepository;

    public List<Estado> listar() {
        return estadoRepository.findAll();
    }

    public Estado buscar(Long id) {
        return estadoRepository.findById(id).orElseThrow(() -> {
            throw new EstadoNaoEncontradoException(id);
        });
    }

    public Estado cadastrar(Estado estado) {
        return estadoRepository.save(estado);
    }

    public Estado editar(Estado estado) {
        buscar(estado.getId());

        return estadoRepository.save(estado);
    }

    public void remover(Long id) {
        try {
            estadoRepository.deleteById(id);
        }  catch (EmptyResultDataAccessException ex) {
            throw new EstadoNaoEncontradoException(id);
        } catch (DataIntegrityViolationException ex) {
            throw new EntidadeEmUsoException("Estado está sendo usado.");
        }
    }
}

package com.paulo.dev.foodapi.api.controller;

import com.paulo.dev.foodapi.domain.exception.EntidadeNaoEncontradaException;
import com.paulo.dev.foodapi.domain.exception.EstadoNaoEncontradoException;
import com.paulo.dev.foodapi.domain.exception.NegocioException;
import com.paulo.dev.foodapi.domain.model.Cidade;
import com.paulo.dev.foodapi.domain.service.CidadeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cidades")
public class CidadeController {

    @Autowired
    private CidadeService cidadeService;

    @GetMapping
    public List<Cidade> listar() {
        return cidadeService.listar();
    }

    @GetMapping("/{id}")
    public Cidade buscar(@PathVariable("id") Long id) {
        return cidadeService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cidade cadastrar(@RequestBody Cidade cidade) {
        try {
            return cidadeService.cadastrar(cidade);
        } catch (EstadoNaoEncontradoException ex) {
            throw new NegocioException(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Cidade editar(@PathVariable("id") Long id, @RequestBody Cidade cidade) {
        try {
            Cidade cidadeAtual = cidadeService.buscar(id);
            BeanUtils.copyProperties(cidade, cidadeAtual, "id");

            return cidadeService.editar(cidadeAtual);
        } catch (EstadoNaoEncontradoException ex) {
            throw new NegocioException(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable("id") Long id) {
        cidadeService.remover(id);
    }
}

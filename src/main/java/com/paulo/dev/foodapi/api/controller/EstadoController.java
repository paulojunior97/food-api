package com.paulo.dev.foodapi.api.controller;

import com.paulo.dev.foodapi.domain.model.Estado;
import com.paulo.dev.foodapi.domain.service.EstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/estados")
public class EstadoController {

    @Autowired
    private EstadoService estadoService;

    @GetMapping
    public ResponseEntity<List<Estado>> listar() {
        return ResponseEntity.ok(estadoService.listar());
    }

    @GetMapping("/{id}")
    public Estado buscar(@PathVariable("id") Long id) {
        return estadoService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Estado cadastrar(@RequestBody @Valid Estado estado) {
        return estadoService.cadastrar(estado);
    }

    @PutMapping("/{id}")
    public Estado editar(@PathVariable("id") Long id, @RequestBody @Valid Estado estado) {
        estado.setId(id);
        return estadoService.editar(estado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable("id") Long id) {
        estadoService.remover(id);
    }
}

package com.paulo.dev.foodapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulo.dev.foodapi.domain.exception.CozinhaNaoEncontradaException;
import com.paulo.dev.foodapi.domain.exception.NegocioException;
import com.paulo.dev.foodapi.domain.model.Restaurante;
import com.paulo.dev.foodapi.domain.service.RestauranteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @GetMapping("/{id}")
    public Restaurante buscar(@PathVariable Long id) {
        return restauranteService.buscar(id);
    }

    @GetMapping
    public List<Restaurante> listar() {
        return restauranteService.listar();
    }

    @GetMapping("/nome")
    public List<Restaurante> consultandoPorNome(@RequestParam String nome) {
        return restauranteService.consultarPorNome(nome);
    }

    @GetMapping("/taxas")
    public List<Restaurante> consultarPorTaxaFrete(@RequestParam String nome,
                                                                   @RequestParam BigDecimal taxaFreteInicial,
                                                                   @RequestParam BigDecimal taxaFreteFinal) {
        return restauranteService.consultarMediaTaxaFrete(nome, taxaFreteInicial, taxaFreteFinal);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurante cadastrar(@RequestBody @Valid Restaurante restaurante) {
        try {
            return restauranteService.cadastrar(restaurante);
        } catch (CozinhaNaoEncontradaException ex) {
            throw new NegocioException(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Restaurante atualizar(@PathVariable Long id, @RequestBody @Valid Restaurante restaurante) {
        try {
            Restaurante restauranteAtual = restauranteService.buscar(id);
            BeanUtils.copyProperties(restaurante, restauranteAtual, "id", "formasPagamento", "endereco", "dataCadastro", "produtos");

            return restauranteService.editar(restauranteAtual);
        } catch (CozinhaNaoEncontradaException ex) {
            throw new NegocioException(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        restauranteService.remover(id);
    }

    @PatchMapping("/{id}")
    public Restaurante atualizarParcial(@PathVariable("id") Long id, @RequestBody Map<String, Object> parcialRestaurante) {
        Restaurante restauranteAtual = restauranteService.buscar(id);
        merge(restauranteAtual, parcialRestaurante);
        return atualizar(id, restauranteAtual);
    }

    private void merge(Restaurante restaurante, Map<String, Object> parcialRestaurante) {
        ObjectMapper objectMapper = new ObjectMapper();
        Restaurante dadosOrigem = objectMapper.convertValue(parcialRestaurante, Restaurante.class);

        parcialRestaurante.forEach((nomePropriedade, valorPropriedade) -> {
            Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade);
            field.setAccessible(true);

            Object novoValor = ReflectionUtils.getField(field, dadosOrigem);
            ReflectionUtils.setField(field, restaurante, novoValor);
        });
    }
}

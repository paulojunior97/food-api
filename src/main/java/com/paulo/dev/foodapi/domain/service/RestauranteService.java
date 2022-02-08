package com.paulo.dev.foodapi.domain.service;

import com.paulo.dev.foodapi.domain.exception.RestauranteNaoEncontradoException;
import com.paulo.dev.foodapi.domain.model.Cozinha;
import com.paulo.dev.foodapi.domain.model.Restaurante;
import com.paulo.dev.foodapi.domain.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private CozinhaService cozinhaService;

    public Restaurante cadastrar(Restaurante restaurante) {
        Cozinha cozinha = cozinhaService.buscar(restaurante.getCozinha().getId());
        restaurante.setCozinha(cozinha);
        return restauranteRepository.save(restaurante);
    }

    public Restaurante editar(Restaurante restaurante) {
        Cozinha cozinha = cozinhaService.buscar(restaurante.getCozinha().getId());
        restaurante.setCozinha(cozinha);

        return restauranteRepository.save(restaurante);
    }

    public Restaurante buscar(Long id) {
        return restauranteRepository.findById(id).orElseThrow(() -> {
            throw new RestauranteNaoEncontradoException(id);
        });
    }

    public List<Restaurante> listar() {
        return restauranteRepository.findAll();
    }

    public List<Restaurante> consultarPorNome(String nome) {
        return restauranteRepository.consultarPorNome(nome);
    }

    public List<Restaurante> consultarMediaTaxaFrete(String nome, BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal) {
        return restauranteRepository.find(nome, taxaFreteInicial, taxaFreteFinal);
    }

    public void remover(Long id) {
        try {
            restauranteRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new RestauranteNaoEncontradoException(id);
        }
    }
}

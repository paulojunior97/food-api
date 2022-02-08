package com.paulo.dev.foodapi.domain.repository;

import com.paulo.dev.foodapi.domain.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long>, RestauranteRepositoryCustomize {

    List<Restaurante> consultarPorNome(String nome);
}

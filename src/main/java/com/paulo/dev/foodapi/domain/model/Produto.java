package com.paulo.dev.foodapi.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Produto {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false)
    private String descricao;

    @NotNull
    @Column(nullable = false)
    private BigDecimal preco;

    @NotNull
    @Column(nullable = false)
    private Boolean ativo;

//    @JsonIgnoreProperties("hibernateLazyInitializer")
    @ManyToOne //(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Restaurante restaurante;
}

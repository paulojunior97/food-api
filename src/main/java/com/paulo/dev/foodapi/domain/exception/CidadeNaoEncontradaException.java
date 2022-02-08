package com.paulo.dev.foodapi.domain.exception;

public class CidadeNaoEncontradaException extends EntidadeNaoEncontradaException {

    public CidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

    public CidadeNaoEncontradaException(Long id) {
        this(String.format("Não existe cidade com ID %d", id));
    }
}

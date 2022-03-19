package com.paulo.dev.foodapi.api.controller.exceptionhandler;

import lombok.Getter;

@Getter
public enum ProblemTypeEnum {

    MENSAGEM_INCOMPREENSIVEL("/mensagem-incompreensivel", "Mensagem incompreensível"),
    RECURSO_NAO_ENCONTRADO("/recurso-nao-encontrado", "Recurso não encontrado"),
    ENTIDADE_EM_USO("/entidade-em-uso", "Entidade em uso"),
    ERRO_NEGOCIO("/erro-negocio", "Violação de regra de negócio"),
    PARAMETRO_INVALIDO("/parametro-invalido", "Parâmetro inválido"),
    ERRO_DE_SISTEMA("erro-de-sistema", "Erro de sistema");

    private String title;
    private String uri;

    ProblemTypeEnum(String path, String title) {
        this.uri = String.format("https://apifood.com.br%s", path);
        this.title = title;
    }
}

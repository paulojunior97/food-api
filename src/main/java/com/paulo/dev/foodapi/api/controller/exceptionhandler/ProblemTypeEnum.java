package com.paulo.dev.foodapi.api.controller.exceptionhandler;

import lombok.Getter;

@Getter
public enum ProblemTypeEnum {

    MENSAGEM_INCOMPREENSIVEL("/mensagem-incompreensivel", "Mensagem incompreensível"),
    ENTIDADE_NAO_ENCONTRADA("/entidade-nao-encontrada", "Entidade não encontrada"),
    ENTIDADE_EM_USO("/entidade-em-uso", "Entidade em uso"),
    ERRO_NEGOCIO("/erro-negocio", "Violação de regra de negócio");

    private String title;
    private String uri;

    ProblemTypeEnum(String path, String title) {
        this.uri = String.format("https://apifood.com.br%s", path);
        this.title = title;
    }
}

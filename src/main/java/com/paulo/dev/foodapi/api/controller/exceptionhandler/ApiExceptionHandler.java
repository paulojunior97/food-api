package com.paulo.dev.foodapi.api.controller.exceptionhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.paulo.dev.foodapi.domain.exception.EntidadeEmUsoException;
import com.paulo.dev.foodapi.domain.exception.EntidadeNaoEncontradaException;
import com.paulo.dev.foodapi.domain.exception.NegocioException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String MSG_ERRO_SISTEMA_USER_MESSAGE = "Ocorreu um erro interno inesperado no sistema. Tente novamente e se o problema persistir," +
            " entre em contato com o administrador do sistema.";

    @Autowired
    private MessageSource messageSource;

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        if (rootCause instanceof InvalidFormatException) {
            return handleInvalidFormat((InvalidFormatException) rootCause, headers, status, request);
        }
        String detail = "O corpo da requisição está inválido. Verifique o erro de sintaxe.";
        Problem problem = createProblemBuilder(status, ProblemTypeEnum.MENSAGEM_INCOMPREENSIVEL, detail)
                .userMessage(MSG_ERRO_SISTEMA_USER_MESSAGE)
                .build();
        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    public ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return handleMethodArgumentTypeMismatch((MethodArgumentTypeMismatchException) ex, headers, status, request);
        }
        return super.handleTypeMismatch(ex, headers, status, request);
    }

    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ProblemTypeEnum problemType = ProblemTypeEnum.RECURSO_NAO_ENCONTRADO;

        String detail = String.format("O recurso %s, que você tentou acessar, é inexistente.", ex.getRequestURL());

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ProblemTypeEnum problemType = ProblemTypeEnum.DADOS_INVALIDOS;

        String detail = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";

        BindingResult bindingResult = ex.getBindingResult();
        List<Problem.Field> problemFields = bindingResult.getAllErrors().stream().map(objectError -> {
            String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
            String name = null;
            if (objectError instanceof FieldError) {
                name = ((FieldError) objectError).getField();
            }
            return Problem.Field.builder()
                    .name(name)
                    .userMessage(message)
                    .build();
        }).collect(Collectors.toList());
        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .fields(problemFields)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<?> handleEntidadeNaoEncontradaException(EntidadeNaoEncontradaException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        Problem problem = createProblemBuilder(status, ProblemTypeEnum.RECURSO_NAO_ENCONTRADO, ex.getMessage())
                .userMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<?> handleNegocioException(NegocioException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Problem problem = createProblemBuilder(status, ProblemTypeEnum.ERRO_NEGOCIO, ex.getMessage())
                .userMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(EntidadeEmUsoException.class)
    public ResponseEntity<?> handleEntidadeEmUsoException(EntidadeEmUsoException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        Problem problem = createProblemBuilder(status, ProblemTypeEnum.ENTIDADE_EM_USO, ex.getMessage())
                .userMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemTypeEnum problemType = ProblemTypeEnum.ERRO_DE_SISTEMA;
        String detail = "Ocorreu um erro interno inesperado no sistema. Tente novamente e se o problema persistir, entre em contato com o administrador do sistema.";

        ex.printStackTrace();

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (body == null) {
            body = Problem.builder()
                    .status(status.value())
                    .title(status.getReasonPhrase())
                    .userMessage(MSG_ERRO_SISTEMA_USER_MESSAGE)
                    .timestamp(LocalDateTime.now())
                    .build();
        } else if (body instanceof String) {
            body = Problem.builder()
                    .status(status.value())
                    .title((String) body)
                    .userMessage(MSG_ERRO_SISTEMA_USER_MESSAGE)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private ResponseEntity<Object> handleInvalidFormat(InvalidFormatException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ProblemTypeEnum problemType = ProblemTypeEnum.MENSAGEM_INCOMPREENSIVEL;
        String path = ex.getPath().stream().map(ref -> ref.getFieldName()).collect(Collectors.joining("."));
        String detail = String.format("A propriedade '%s' recebeu o valor '%s', que é de um tipo inválido. Corrija e informe um valor compatível com o tipo '%s'",
        path, ex.getValue(), ex.getTargetType().getSimpleName()
                );

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(MSG_ERRO_SISTEMA_USER_MESSAGE)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpHeaders headers,HttpStatus status, WebRequest request) {

        ProblemTypeEnum problemType = ProblemTypeEnum.PARAMETRO_INVALIDO;

        String detail = String.format("O parâmetro de URL '%s' recebeu o valor '%s', que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(MSG_ERRO_SISTEMA_USER_MESSAGE)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private Problem.ProblemBuilder createProblemBuilder(HttpStatus status, ProblemTypeEnum problemType, String detail) {

        return Problem.builder()
                .status(status.value())
                .type(problemType.getUri())
                .title(problemType.getTitle())
                .detail(detail)
                .timestamp(LocalDateTime.now());
    }
}

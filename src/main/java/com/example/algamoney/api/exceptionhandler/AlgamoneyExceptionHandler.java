package com.example.algamoney.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
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
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/*
 * Por herdar ResponseEntityExceptionHandler, esta classe captura exeções da
 * ResponseEntity.
 * 
 * Esta classe precisa ser um @ControllerAdvice, para ser um componente do Spring
 * e observar toda a aplicação.
 * 
 * Obs. Nesta classe estão sendo colocadas as exceções mais gerais, que qualquer 
 * controlador poderia lançar de qualquer lugar. As exceções mais especificas podem ser
 * tratadas no próprio controlador.
 * */
@ControllerAdvice
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler {

	/*Esta classe já consegue pegar direto do arquivo
	 * messages.properties, as mensagens.*/
	@Autowired
	private MessageSource messageSource;
	
	/*Sobreescrevendo este método, eu posso tratar quando o cliente enviar em
	 * um post, um corpo que não existe.
	 * Para funcionar é necessário que a configuação no application.proporties
	 * esteja correta, que é a configuração que fala que não pode enviar uma
	 * requisição errada.
	 * 
	 * spring.jackson.deserialization.fail-on-unknown-properties=true
	 * */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		//Aqui pega a mensagem em messages.properties.
		String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocaleContext().getLocale()/*Para pegar o locale corrente.*/);
		String mensagemDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	/*Este método é o que valida quando os argumentos são validados com Bean Validation
	 * (@NotNull, @Size) e tem algum argumento que não é valido.
	 * Para ajudar com as mensagens, podemos usar o ValidationMessages.properties 
	 * junto com o messages.properties.*/
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<Erro> erros = criarListaDeErros(ex.getBindingResult());
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	/**
	 * Este método trata quando um recurso não é encontrado na hora do delede.
	 * **/
	@ExceptionHandler({EmptyResultDataAccessException.class})
	public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
		String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		//Tem que instanciar o HttpHeaders(), porque se passar pelo parâmetro do método vai dar erro.
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	@ExceptionHandler({ConstraintViolationException.class})
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
		String mensagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale());
		//A classe ExceptionUtils, deixa o erro mais amigável e mais fácil de se compreender.
		String mensagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		//Tem que instanciar o HttpHeaders(), porque se passar pelo parâmetro do método vai dar erro.
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	/**
	 * Método que ajuda a criar um lista de erros.
	 * **/
	public List<Erro> criarListaDeErros(BindingResult bindingResult){
		List<Erro> erros = new ArrayList<>();
		
		for(FieldError fieldError : bindingResult.getFieldErrors()) {
			String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mensagemDesenvolvedor = fieldError.toString();
			
			erros.add(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		}
		
		return erros;
	}
	
	/**
	 * Esta classe será usada para mostrar os erros de um forma mais amigável.
	 * **/
	public static class Erro {
		private String mensagemUsuario;
		private String mensagemDesenvolvedor;
		
		public Erro(String mensagemUsuario, String mensagemDesenvolvedor) {
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}
		
		public String getMensagemUsuario() {
			return mensagemUsuario;
		}
		
		public String getMensagemDesenvolvedor() {
			return mensagemDesenvolvedor;
		}
	}
}
package com.example.algamoney.api.event;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

/**
 * Esta classe trabalha em conjunto com mais duas classes.
 * RecursoCriadoListener e ApplicationEventPublisher.
 * 
 * A classe ApplicationEventPublisher executa a chamada da classe RecursoCriadoListener.
 * **/
public class RecursoCriadoEvent extends ApplicationEvent {

	private static final long serialVersionUID = -8456496638934868695L;
	
	private HttpServletResponse response;
	private Long codigo;
	
	public RecursoCriadoEvent(Object source, HttpServletResponse response, Long codigo) {
		super(source);
		this.response = response;
		this.codigo = codigo;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Long getCodigo() {
		return codigo;
	}
}
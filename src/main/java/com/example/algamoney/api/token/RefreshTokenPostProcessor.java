package com.example.algamoney.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

/*
 * Esta classe server para interceptar respostas. No nosso caso ele vai interceptar quando
 * a resposta for OAuth2AccessToken. Por exemplo, se em um controller o retorno de uma requisição
 * for uma classe Xpto, se eu criar uma classe como esta, mais no lugar de OAuth2AccessToken eu colocar
 * Xpto, a classe vai interceptar todas as respostas com a classe Xpto.
 * 
 * O Criado do curso só descobriu que a classe a ser intercptada é a OAuth2AccessToken, porque 
 * ele olhou o código fonte do Spring Security OAuth2.
 * */
@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;
	
	/*
	 * Esta classe tem dois métodos, o supports e o beforeBodyWrite, o beforeBodyWrite só é executado
	 * caso o método supports retorne true.
	 * */
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		/*A classe OAuth2AccessToken pode ser retornada em outros momentos que não seja no retorno do token,
		 * por isso é preciso fazer a seguinte validação.
		 * O Criador do curso só descobriu que deveria ser feito assim, pois ele fuçou no código fonte do Spring Security OAuth2
		 * , ou debugou para entender o que ele estava retornando.*/
		return returnType.getMethod().getName().equals("postAccessToken");
	}

	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
		
		/*Ele só conseguiu descobrir que era este token olhando o código fonte
		 * do Spring Security OAuth2*/
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
		
		String refreshToken = body.getRefreshToken().getValue();
		adicionarRefreshTokenNoCookie(refreshToken, req, resp);
		removerRefreshTokenDoBody(token);
		
		return body;
	}

	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null);
	}

	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		//Para que seja acessível só em http
		refreshTokenCookie.setHttpOnly(true);
		//Se ele deve ser um Cookie seguro ou não. Usar false em Local e true em produção
		refreshTokenCookie.setSecure(algamoneyApiProperty.getSeguranca().isEnableHttps());
		//Para qual caminho o Cookie deve ser enviado para o browser automaticamente
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token");
		//Em quanto tempo o Cookie vai expirar 2592000 (30 dias)
		refreshTokenCookie.setMaxAge(2592000);
		resp.addCookie(refreshTokenCookie);
	}
}
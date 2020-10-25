package com.example.algamoney.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.example.algamoney.api.config.token.CustomTokenEnhancer;

@Profile("oauth-security")
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception{
		clients.inMemory()
				.withClient("angular")
				.secret("@angul@r0")
				/*Pode-se definir escopos diferentes para clientes diferentes.
				 * Não são as strings read, write que definem o que o cliente
				 * vai fazer, mais sim o que fazemos depois com elas.*/
				.scopes("read", "write")
				/*Quer dizer que vamos usar password flow, ou seja, vai precisar de 
				 * usuário e senha para adquirir o token.
				 * No caso do nosso exemplo o usuário e senha serão passados
				 * dentro da própia aplicação angular. Vamos fazer assim
				 * pois a aplicação é de nossa interira confiança
				 * , então neste caso faz sentido fazer assim.*/
				.authorizedGrantTypes("password", "refresh_token")
				//Quanto tempo nosso token vai funcionar.
				.accessTokenValiditySeconds(1800)
				//Tempo de validade do refresh token, no caso um dia inteiro.
				.refreshTokenValiditySeconds(3600 * 24)
			.and()
				.withClient("mobile")
				.secret("m0b1l30")
				.scopes("read")
				.authorizedGrantTypes("password", "refresh_token")
				.accessTokenValiditySeconds(1800)
				.refreshTokenValiditySeconds(3600 * 24);
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		//Este é um token com mais detalhes
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		endpoints
			.tokenStore(tokenStore())
			.tokenEnhancer(tokenEnhancerChain)
			//Aqui quer dizer que o refresh token só pode ser usado uma vez.
			.reuseRefreshTokens(false)
			.authenticationManager(authenticationManager);
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("algaworks");//Chave que valida o token
		return accessTokenConverter;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}
	
	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}
}
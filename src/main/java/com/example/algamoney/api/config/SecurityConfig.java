package com.example.algamoney.api.config;

/*import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;*/

//@Configuration//A notação @Configuration não é necessária nesta classe, pois @EnableWebSecurity já tem esta notação, mais pode ajudar na hora da visualização.
//@EnableWebSecurity
public class SecurityConfig /* extends WebSecurityConfigurerAdapter */ {

	/*
	 * Classe de configuração quando se usa Spring Security com autenticação Basic
	 * */
	
	/*
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//Nesta caso por ser um teste o usuário vai estar em memória. role é uma permissão.
		auth.inMemoryAuthentication()
			.withUser("admin").password("{noop}admin").roles("ROLE");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				//Qualquer um pode acessar categorias
				.antMatchers("/categorias").permitAll()
				//Todos os outros locais vão precisar se autenticar
				.anyRequest().authenticated()
				.and()
			//Tipo de validação basic
			.httpBasic().and()
			//Aqui estou dizendo que a aplicação não vai guardar sessão
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			//csrf é para evitar injection por javascript em um serviço web, mais como nossa aplicação não tem a parte web, torna-se desnecessário.
			.csrf().disable();
			
	}
	*/
}
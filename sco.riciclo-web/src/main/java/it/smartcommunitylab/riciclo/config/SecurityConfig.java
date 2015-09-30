package it.smartcommunitylab.riciclo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationProvider customAuthenticationProvider;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(customAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.headers()
				.frameOptions().disable();
		http
        	.csrf()
				.disable()
			.authorizeRequests()
				.antMatchers("/", "/upload/**", "/console/**", "/aree/**", "/rifiuti/**", "/tipologia/**",
						"/riciclabolario/**", "/puntiraccolta/**", "/calendariraccolta/**", "/istituzioni/**",
						"/gestori/**", "/tipo-utenza/**", "/tipo-profilo/**", "/tipo-rifiuto/**", 
						"/tipo-raccolta/**", "/tipo-punto/**", "/colori/**")
					.authenticated()
				.anyRequest()
					.permitAll();
		http.formLogin().loginPage("/login").permitAll().and().logout().permitAll();
	}
}

package springboot.librarysystem.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	@Bean
	public JsonAuthHandlers.JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint() {
		return new JsonAuthHandlers.JsonAuthenticationEntryPoint();
	}

	@Bean
	public JsonAuthHandlers.JsonAccessDeniedHandler jsonAccessDeniedHandler() {
		return new JsonAuthHandlers.JsonAccessDeniedHandler();
	}

	@Bean
	public UserDetailsService userDetailsService(springboot.librarysystem.service.UserService userService) {
		return username -> userService.loadUserByUsername(username);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public org.springframework.security.web.SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.headers().frameOptions().disable().and()
			.authorizeRequests()
				.antMatchers(
					"/swagger-ui.html",
					"/swagger-ui/**",
					"/v3/api-docs/**"
				).permitAll()
				.antMatchers("/api/books/search").permitAll()
				.antMatchers("/api/books/**").hasRole("LIBRARIAN")
				.antMatchers("/api/libraries/**").hasRole("LIBRARIAN")
				.anyRequest().permitAll()
			.and()
			.httpBasic()
			.authenticationEntryPoint(jsonAuthenticationEntryPoint())
			.and()
			.exceptionHandling().accessDeniedHandler(jsonAccessDeniedHandler());
		return http.build();
	}
}

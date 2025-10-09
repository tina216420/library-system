package springboot.librarysystem.config;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
		http.csrf(csrf -> csrf.disable())
			.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
			.authorizeHttpRequests(authz -> authz
				.requestMatchers(
					"/swagger-ui.html",
					"/swagger-ui/**",
					"/v3/api-docs/**"
				).permitAll()
				.requestMatchers("/api/books/search").permitAll()
				.requestMatchers("/api/books/**").hasRole("LIBRARIAN")
				.requestMatchers("/api/libraries/**").hasRole("LIBRARIAN")
				.anyRequest().permitAll()
			)
			.httpBasic(basic -> basic
				.authenticationEntryPoint(jsonAuthenticationEntryPoint())
			)
			.exceptionHandling(ex -> ex
				.accessDeniedHandler(jsonAccessDeniedHandler())
			);
		return http.build();
	}
}

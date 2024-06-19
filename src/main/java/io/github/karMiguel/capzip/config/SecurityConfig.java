package io.github.karMiguel.capzip.config;

import io.github.karMiguel.capzip.security.JwtTokenFilter;
import io.github.karMiguel.capzip.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@Configuration
	public class SecurityConfig {
	@Autowired
	private JwtTokenProvider tokenProvider;


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}



	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		JwtTokenFilter customFilter = new JwtTokenFilter(tokenProvider);

		//@formatter:off
		return http
				.httpBasic(basic -> basic.disable())
				.csrf(csrf -> csrf.disable())
				.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)
				.sessionManagement(
						session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(
						authorizeHttpRequests -> authorizeHttpRequests
								.requestMatchers(
										antMatcher("/api/v1/user/**"),
										antMatcher("/auth/signin"),
										antMatcher("/auth/refresh/**"),
										antMatcher("/swagger-ui/**"),
										antMatcher("/v3/api-docs/**"),
										antMatcher("/api/v1/total/**"),
										antMatcher("/api/v1/link/shorten-link-no-auth")

								).permitAll()
								.requestMatchers(HttpMethod.GET,"/**").permitAll()
								.requestMatchers("/api/v1/link/**").authenticated()
								.requestMatchers("/api/v1/clicks/**").authenticated()
								.requestMatchers("/users").denyAll()
				)
				.cors(cors -> {})
				.build();
		//@formatter:on
	}

	}
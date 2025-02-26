package com.trillion.tikitaka.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trillion.tikitaka.domain.member.application.MemberDomainService;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.global.security.application.CustomUserDetailsService;
import com.trillion.tikitaka.global.security.filter.CustomAuthenticationFilter;
import com.trillion.tikitaka.global.security.filter.CustomAuthenticationProvider;
import com.trillion.tikitaka.global.security.filter.CustomLogoutFilter;
import com.trillion.tikitaka.global.security.filter.JwtAuthenticationFilter;
import com.trillion.tikitaka.global.security.handler.CustomAccessDeniedHandler;
import com.trillion.tikitaka.global.security.handler.CustomAuthenticationEntryPointHandler;
import com.trillion.tikitaka.global.security.handler.CustomAuthenticationFailureHandler;
import com.trillion.tikitaka.global.security.handler.CustomAuthenticationSuccessHandler;
import com.trillion.tikitaka.global.security.jwt.JwtService;
import com.trillion.tikitaka.global.security.jwt.JwtTokenProvider;
import com.trillion.tikitaka.global.security.jwt.JwtTokenRepository;
import com.trillion.tikitaka.global.security.jwt.JwtUtil;
import com.trillion.tikitaka.global.security.util.SecurityResponseUtils;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final JwtConfig jwtConfig;
	private final JwtService jwtService;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtTokenRepository jwtTokenRepository;
	private final CustomUserDetailsService userDetailsService;
	private final MemberDomainService memberDomainService;
	private final MemberRepository memberRepository;
	private final CustomAccessDeniedHandler accessDeniedHandler;
	private final CustomAuthenticationEntryPointHandler authenticationEntryPointHandler;
	private final SecurityResponseUtils securityResponseUtils;
	private final ObjectMapper objectMapper;
	private final BCryptPasswordEncoder bcryptPasswordEncoder;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.cors((cors) -> cors.configurationSource(corsConfigurationSource()))
			.httpBasic(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
				.requestMatchers("/login", "/api/registrations", "/api/reissue").permitAll()
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			)

			.exceptionHandling((exception) -> exception
				.authenticationEntryPoint(authenticationEntryPointHandler)
				.accessDeniedHandler(accessDeniedHandler)
			)

			.addFilterAt(customLogoutFilter(), LogoutFilter.class)
			.addFilterAfter(customAuthenticationFilter(), LogoutFilter.class)
			.addFilterBefore(jwtAuthenticationFilter(), CustomAuthenticationFilter.class)

			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(customAuthenticationProvider());
	}

	@Bean
	public CustomAuthenticationFilter customAuthenticationFilter() {
		CustomAuthenticationFilter filter = new CustomAuthenticationFilter(objectMapper);
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler());
		filter.setAuthenticationFailureHandler(customAuthenticationFailureHandler());
		return filter;
	}

	@Bean
	public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
		return new CustomAuthenticationSuccessHandler(jwtUtil, jwtConfig, jwtTokenProvider, objectMapper);
	}

	@Bean
	public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
		return new CustomAuthenticationFailureHandler(securityResponseUtils);
	}

	@Bean
	public CustomAuthenticationProvider customAuthenticationProvider() {
		CustomAuthenticationProvider provider = new CustomAuthenticationProvider(memberRepository, memberDomainService);
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(bcryptPasswordEncoder);
		return provider;
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtUtil, jwtService, jwtTokenProvider, memberRepository);
	}

	@Bean
	public CustomLogoutFilter customLogoutFilter() {
		return new CustomLogoutFilter(jwtUtil, jwtService, jwtTokenProvider, jwtTokenRepository);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
		corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		corsConfiguration.setAllowedHeaders(List.of("*"));
		corsConfiguration.setExposedHeaders(List.of("Authorization"));
		corsConfiguration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}
}

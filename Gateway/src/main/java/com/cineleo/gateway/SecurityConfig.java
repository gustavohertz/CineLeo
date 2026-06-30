package com.cineleo.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String ISSUER = "auth-service";

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // infra e degradação não exigem token
                        .requestMatchers("/actuator/**", "/fallback/**").permitAll()
                        // login e cadastro precisam ser públicos (é onde se obtém o token)
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/login", "/api/usuarios/create").permitAll()
                        // catálogo e recomendações são consultas públicas
                        .requestMatchers(HttpMethod.GET,
                                "/api/eventos/filmes/**", "/api/eventos/salas/**", "/api/eventos/sessoes/**",
                                "/api/recomendacoes/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    // Decoder que busca a chave pública no JWKS do Usuarios e valida
    // assinatura RS256, expiração e o issuer esperado.
    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        OAuth2TokenValidator<Jwt> comIssuer = JwtValidators.createDefaultWithIssuer(ISSUER);
        decoder.setJwtValidator(comIssuer);
        return decoder;
    }
}

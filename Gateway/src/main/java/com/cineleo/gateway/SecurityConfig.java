package com.cineleo.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

        private static final String ISSUER = "auth-service";

    @Value("${jwt.secret}")
    private String jwtSecret;

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(auth -> auth
                                                // Libera todas as rotas de notificações, pagamentos e usuários (GET)
                                                .requestMatchers("/api/notificacoes/**", "/api/pagamentos/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/usuarios/**").permitAll()
                                                // Libera actuator e fallback
                                                .requestMatchers("/actuator/**", "/fallback/**").permitAll()
                                                // login e cadastro públicos
                                                .requestMatchers(HttpMethod.POST, "/api/usuarios/login",
                                                                "/api/usuarios/create")
                                                .permitAll()
                                                // catálogo e recomendações são consultas públicas
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/eventos/filmes/**", "/api/eventos/salas/**",
                                                                "/api/eventos/sessoes/**",
                                                                "/api/recomendacoes/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
                return http.build();
        }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        OAuth2TokenValidator<Jwt> validator =
                JwtValidators.createDefaultWithIssuer(ISSUER);

        decoder.setJwtValidator(validator);

        return decoder;
    }
}

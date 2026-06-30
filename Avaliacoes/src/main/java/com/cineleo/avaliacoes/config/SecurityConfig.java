package com.cineleo.avaliacoes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwks-uri}")
    private String jwksUri;

    /**
     * Define o JwtDecoder manualmente com NimbusJwtDecoder.
     *
     * Por que isso e necessario?
     * A configuracao automatica do Spring tenta buscar as chaves publicas RSA
     * do Usuarios Service (jwks-uri) no momento em que a aplicacao sobe.
     * Se o Usuarios Service nao estiver no ar, a aplicacao falha ao iniciar.
     *
     * Com NimbusJwtDecoder.withJwkSetUri().build(), a conexao so acontece
     * na PRIMEIRA requisicao que chega com um JWT — nao na inicializacao.
     * Isso e chamado de inicializacao "lazy" (preguicosa).
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/avaliacoes/filme/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/avaliacoes/filmes/ranking").permitAll()
                .requestMatchers("/health-check").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return http.build();
    }
}

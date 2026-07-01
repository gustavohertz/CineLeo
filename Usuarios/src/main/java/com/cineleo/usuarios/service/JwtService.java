package com.cineleo.usuarios.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.cineleo.usuarios.dto.UsuarioAutenticadoResponseDTO;
import com.cineleo.usuarios.entity.UsuarioEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final String ISSUER = "auth-service";

    private final long accessTokenExpirationSeconds;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-seconds:3600}") long accessTokenExpirationSeconds
    ) {
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;

        this.algorithm = Algorithm.HMAC256(secret);

        this.verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
    }

    public String gerarAccessToken(UsuarioEntity usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(accessTokenExpirationSeconds);

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(usuario.getId().toString())
                .withClaim("name", usuario.getNome())
                .withClaim("email", usuario.getEmail())
                .withClaim("roles", usuario.getRoles().stream().toList())
                .withIssuedAt(Date.from(agora))
                .withExpiresAt(Date.from(expiracao))
                .sign(algorithm);
    }

    public DecodedJWT validarToken(String token) {
        return verifier.verify(token);
    }

    public String extrairUsuario(String token) {
        return validarToken(token).getSubject();
    }

    public boolean tokenValido(String token) {
        try {
            validarToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }

    public UsuarioAutenticadoResponseDTO verificarToken(String token) {
        DecodedJWT decoded = validarToken(token);

        return new UsuarioAutenticadoResponseDTO(
                decoded.getSubject(),
                decoded.getClaim("name").asString(),
                decoded.getClaim("email").asString(),
                decoded.getClaim("roles").asList(String.class)
        );
    }
}
package com.cineleo.usuarios.controller;

import com.cineleo.usuarios.service.JwtService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/.well-known")
public class JwtController {
    private static final String KEY_ID = "auth-token-1";

    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/jwks.json")
    public Map<String, Object> getJwks() {
        RSAPublicKey publicKey = jwtService.getPublicKey();

        Map<String, Object> key = Map.of(
                "kty", "RSA",
                "kid", KEY_ID,
                "use", "sig",
                "alg", "RS256",
                "n", base64UrlUnsigned(publicKey.getModulus()),
                "e", base64UrlUnsigned(publicKey.getPublicExponent())
        );

        return Map.of("keys", List.of(key));
    }

    private String base64UrlUnsigned(BigInteger value) {
        byte[] bytes = value.toByteArray();

        if (bytes[0] == 0) {
            byte[] unsignedBytes = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, unsignedBytes, 0, unsignedBytes.length);
            bytes = unsignedBytes;
        }

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
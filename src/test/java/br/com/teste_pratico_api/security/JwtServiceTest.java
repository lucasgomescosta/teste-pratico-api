package br.com.teste_pratico_api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secret", "minha-chave-secreta-super-segura-123456");
        ReflectionTestUtils.setField(jwtService, "expiration", 1800000L); // 30 min
    }

    @Test
    @DisplayName("Deve gerar token válido")
    void deveGerarToken() {
        String token = jwtService.gerarToken("admin");

        assertNotNull(token);
    }

    @Test
    @DisplayName("Deve extrair username do token")
    void deveExtrairUsername() {
        String token = jwtService.gerarToken("admin");

        String username = jwtService.extrairUsername(token);

        assertEquals("admin", username);
    }

    @Test
    @DisplayName("Deve validar token válido")
    void deveValidarToken() {
        String token = jwtService.gerarToken("admin");

        boolean valido = jwtService.tokenValido(token);

        assertTrue(valido);
    }
}

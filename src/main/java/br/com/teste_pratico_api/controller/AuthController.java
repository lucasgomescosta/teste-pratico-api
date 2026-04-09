package br.com.teste_pratico_api.controller;

import br.com.teste_pratico_api.api.ApiPaths;
import br.com.teste_pratico_api.domain.dto.request.LoginRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.LoginResponseDTO;
import br.com.teste_pratico_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gerenciador de autenticação do Spring.
 */
@RestController
@RequestMapping(ApiPaths.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Realiza login do usuário e retorna token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        // Autentica usuário
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getLogin(), dto.getSenha())
        );

        // Gera token JWT
        String token = jwtService.gerarToken(dto.getLogin());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setTipo("Bearer");
        response.setExpiraEm(1800L);
        return ResponseEntity.ok(response);
    }
}

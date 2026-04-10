package br.com.teste_pratico_api.security;

import br.com.teste_pratico_api.exception.ErrorTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import org.springframework.security.core.AuthenticationException;
import java.io.IOException;
import java.time.Instant;

/**
 * Responsável por retornar uma resposta padronizada quando o usuário
 * tenta acessar um recurso protegido sem estar autenticado.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        ErrorTemplate errorTemplate = new ErrorTemplate();
        errorTemplate.setError("Não autenticado. É necessário realizar autenticação para acessar este recurso.");
        errorTemplate.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorTemplate.setMessage(authException.getMessage());
        errorTemplate.setTimestamp(Instant.now());
        errorTemplate.setPath(request.getRequestURI());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(errorTemplate));
    }

}

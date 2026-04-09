package br.com.teste_pratico_api.config;

import br.com.teste_pratico_api.api.ApiPaths;
import br.com.teste_pratico_api.security.CustomAuthenticationEntryPoint;
import br.com.teste_pratico_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração de segurança da aplicação.
 * Define regras de autenticação, autorização e filtros.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // API stateless, não usa sessão nem CSRF tradicional
                .csrf(csrf -> csrf.disable())
                // Habilita CORS
                .cors(Customizer.withDefaults())
                // Define sessão stateless (JWT)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Tratamento customizado de 401
                .exceptionHandling(eh -> eh.authenticationEntryPoint(customAuthenticationEntryPoint))

                // Regras de autorização
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ApiPaths.AUTH + "/**").permitAll()
                        .anyRequest().authenticated()
                )

                // Adiciona filtro JWT antes do filtro padrão de autenticação
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Encoder de senha usando BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Gerenciador de autenticação do Spring.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}

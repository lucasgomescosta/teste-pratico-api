package br.com.teste_pratico_api.service;

import br.com.teste_pratico_api.domain.entity.Usuario;
import br.com.teste_pratico_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementação do Spring Security responsável por carregar os dados do usuário
 * durante o processo de autenticação.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Carrega o usuário a partir do login informado.
     *
     * @param username login do usuário
     * @return UserDetails com credenciais e permissões
     * @throws UsernameNotFoundException caso não encontre
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        // Converte role para padrão do Spring: ROLE_ADMIN
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRole())
        );

        return new User(
                usuario.getLogin(),
                usuario.getSenha(),
                authorities
        );
    }
}

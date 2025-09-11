package br.com.alura.adopet.api.infra.filters;

import br.com.alura.adopet.api.infra.jwt.JwtService;
import br.com.alura.adopet.api.domain.model.Usuario;
import br.com.alura.adopet.api.domain.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Pega o header Authorization
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Se não tiver token, continua a cadeia (não autenticado)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " do token
        String token = header.substring(7);

        // Valida token
        if (!jwtService.validarToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Pega email do token
        String email = jwtService.getEmail(token);

        // Busca usuário no banco
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Aqui você pode adicionar roles/authorities se quiser
            List<SimpleGrantedAuthority> authorities = List.of(); // vazio por enquanto

            // Cria autenticação e adiciona no SecurityContext
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(usuario, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }
}

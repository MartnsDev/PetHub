package br.com.alura.adopet.api.infra.security;

import br.com.alura.adopet.api.infra.filters.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Indica que essa classe define configurações do Spring
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter; // Filtro JWT personalizado para autenticação

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Ativa CORS (permite chamadas de outros domínios, ex: frontend separado)
                .cors()
                .and()

                // Desativa CSRF (não é necessário em APIs REST que usam JWT)
                .csrf(csrf -> csrf.disable())

                // Configuração de autorização para as requisições
                .authorizeHttpRequests(auth -> auth

                        // -------------------- Endpoints Públicos --------------------
                        // Autenticação e cadastro não exigem login
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/cadastro").permitAll()
                        // Recuperação de senha
                        .requestMatchers(HttpMethod.POST, "/api/auth/solicitar-codigo").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/verificar-codigo").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/auth/recuperar").permitAll()
                        // Listagem pública de abrigos e pets
                        .requestMatchers(HttpMethod.GET, "/abrigos/**", "/pets/disponiveis").permitAll()
                        .requestMatchers("/uploads/**").permitAll() // libera acesso às imagens
                        // Documentação da API (Swagger)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // -------------------- Endpoints Protegidos --------------------
                        // Somente usuários autenticados podem excluir abrigos
                        .requestMatchers(HttpMethod.DELETE, "/abrigos/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/pets").authenticated()
                        // Obs: este está liberado, mas poderia ser protegido se necessário
                        .requestMatchers(HttpMethod.POST, "/abrigos/*/pets").permitAll()

                        // -------------------- Qualquer outra requisição precisa de autenticação --------------------
                        .anyRequest().authenticated()
                )

                // Adiciona o filtro JWT antes do filtro padrão de autenticação do Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Retorna a configuração de segurança finalizada
        return http.build();
    }

    // Bean para codificação de senhas (BCrypt é a escolha padrão e segura)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para AuthenticationManager (necessário para autenticação via Spring Security)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

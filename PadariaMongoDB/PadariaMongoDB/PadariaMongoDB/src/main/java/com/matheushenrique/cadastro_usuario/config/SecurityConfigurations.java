package com.matheushenrique.cadastro_usuario.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfigurations {

    // Origens extras liberadas no CORS (ex: front aberto pelo IntelliJ em localhost:63342)
    @Value("${app.cors.origens}")
    private List<String> origensPermitidas;

    // Encoder para senhas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Gerenciador de autenticação
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origensPermitidas);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Configurações de segurança: API REST stateless com HTTP Basic
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 401 sem o cabeçalho WWW-Authenticate, para o navegador não abrir a janela de login nativa
        HttpStatusEntryPoint naoAutorizado = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Regras de autorização
                .authorizeHttpRequests(auth -> auth
                        // páginas e arquivos do front
                        .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/img/**", "/favicon.ico", "/error").permitAll()
                        // cadastro e login
                        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()
                        // catálogo é público, alterações só para admin
                        .requestMatchers(HttpMethod.GET, "/padaria", "/padaria/**").permitAll()
                        .requestMatchers("/padaria", "/padaria/**").hasRole("ADMIN")
                        // listagens gerais só para admin
                        .requestMatchers(HttpMethod.GET, "/usuarios", "/compras").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/usuarios").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .httpBasic(basic -> basic.authenticationEntryPoint(naoAutorizado))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(naoAutorizado));

        return http.build();
    }

}

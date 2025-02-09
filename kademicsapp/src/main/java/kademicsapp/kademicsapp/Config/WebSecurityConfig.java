package kademicsapp.kademicsapp.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/alunos").permitAll()  // Permite POST em /api/alunos
                .requestMatchers(HttpMethod.PUT, "/api/treinos/**").permitAll() // Permite PUT em /api/treinos/{id}
                .requestMatchers(HttpMethod.POST, "/api/treinos/util/buscarIds").permitAll()
                .anyRequest().permitAll()   // Permite qualquer outra requisição
            )
            .csrf(csrf -> csrf.disable())  // Desabilita CSRF
            .httpBasic(httpBasic -> httpBasic.disable());  // Desabilita autenticação básica
        return http.build();
    }
}

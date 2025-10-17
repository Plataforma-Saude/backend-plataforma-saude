package plataformaSaude.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.annotation.Order;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1) // prioridade maior: executa antes do OAuth2
    public SecurityFilterChain registerFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/auth/register/**")
                .csrf().disable()
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {

        // Habilita a validação de tokens JWT (Resource Server)
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)));

        return http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF para a API
                .authorizeHttpRequests(auth -> auth
                        // Permite acesso a rotas essenciais para o fluxo OAuth2 e páginas públicas
                        .requestMatchers(
                                "/", "/error", "/h2-console/**",
                                "/oauth2/**", "/login/oauth2/**", "/favicon.ico"
                        ).permitAll()
                        // Qualquer outra requisição deve ser autenticada (via JWT ou Login Social)
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions().sameOrigin())
                .oauth2Login(oauth2 -> oauth2 // Habilita o login via Google
                        .defaultSuccessUrl("/home", true) // Redireciona para /home após login
                        .failureUrl("/error")
                )
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
                .build();
    }


    @Bean
    public KeyPair rsaKeyPair() {
        return generateRsaKey();
    }

    // Gerador de JWT (EMISSOR) - Injeta o KeyPair único
    @Bean
    public JwtEncoder jwtEncoder(KeyPair rsaKeyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) rsaKeyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) rsaKeyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }

    // Decodificador de JWT (CONSUMIDOR) - Injeta o KeyPair único
    @Bean
    public JwtDecoder jwtDecoder(KeyPair rsaKeyPair) {
        // Usa a chave pública da instância única
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) rsaKeyPair.getPublic()).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao gerar o par de chaves RSA", e);
        }
    }
}
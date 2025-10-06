package plataformaSaude.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class OAuth2Config {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OidcUserService oidcUserService) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Desativa proteção CSRF (adequado para APIs REST)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/oauth2/**", "/login/oauth2/**", "/error", "/favicon.ico", "/").permitAll() // Rotas públicas
                        .anyRequest().authenticated() // Todas as outras exigem autenticação
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/home", true) // Após login, vai para /home
                        .failureUrl("/error?error=true") // Em caso de erro
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService())) // Define o serviço de usuário
                )
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll()) // Logout redireciona para raiz
                .build();
    }

    @Bean
    public OidcUserService oidcUserService() {
        return new OidcUserService(); // Serviço padrão para carregar dados do usuário via OIDC
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWKSource<SecurityContext> jwkSource = getJwkSource();
        return new NimbusJwtEncoder(jwkSource); // Encoder para gerar JWTs assinados
    }

    private JWKSource<SecurityContext> getJwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString()) // Identificador único da chave
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey); // Conjunto de chaves JWK
        return new ImmutableJWKSet<>(jwkSet); // Fonte imutável de chaves
    }

    private KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Tamanho da chave para segurança
            return keyPairGenerator.generateKeyPair(); // Gera par público/privado
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao gerar RSA KeyPair", ex);
        }
    }
}
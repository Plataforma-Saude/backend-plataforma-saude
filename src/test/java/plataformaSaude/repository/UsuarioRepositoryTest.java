package plataformaSaude.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import plataformaSaude.model.Usuario;
import plataformaSaude.configuration.OAuth2Config;

import static org.assertj.core.api.Assertions.assertThat;

// Configura o teste para focar na camada JPA
@DataJpaTest(
        // Desabilita a substituição do DataSource, usando a configuração do application.properties
        excludeAutoConfiguration = {
                AutoConfigureTestDatabase.class,
                OAuth2Config.class // Exclui a sua configuração de segurança
        },
        // Filtra para não carregar a configuração de segurança
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = OAuth2Config.class
        )
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test") // Usa o perfil de teste, se você tiver um
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    public void quandoSalvarUsuario_entaoDeveEncontrarPorEmail() {
        // Dado que temos um novo usuário
        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail("joao.silva@exemplo.com");
        novoUsuario.setNomeCompleto("João Silva");

        // Quando salvamos o usuário
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        // Então, o usuário deve ser encontrado no banco
        Usuario usuarioEncontrado = usuarioRepository.findByEmail("joao.silva@exemplo.com");

        assertThat(usuarioEncontrado).isNotNull();
        assertThat(usuarioEncontrado.getEmail()).isEqualTo(usuarioSalvo.getEmail());
    }
}
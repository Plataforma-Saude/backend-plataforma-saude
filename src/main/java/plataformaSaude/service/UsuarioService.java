package plataformaSaude.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void salvarUsuario(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
    }

    public String hashSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

    public boolean validarSenha(Usuario usuario, String senhaDigitada) {
        return passwordEncoder.matches(senhaDigitada, usuario.getSenha());
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario buscarPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf);
    }

    /* Gera token redefinição de senha válido por 24h. */
    @Transactional
    public Optional<Usuario> gerarTokenResetSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) {
            String token = UUID.randomUUID().toString();
            usuario.setResetPasswordToken(token);
            usuario.setResetPasswordTokenExpiryDate(LocalDate.now().plusDays(1));
            usuarioRepository.save(usuario);
            return Optional.of(usuario);
        }
        return Optional.empty();
    }

    /** Redefine a senha se o token for válido.  */
    @Transactional
    public boolean redefinirSenha(String token, String novaSenha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByResetPasswordToken(token);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            boolean tokenValido = usuario.getResetPasswordTokenExpiryDate() != null &&
                    usuario.getResetPasswordTokenExpiryDate().isAfter(LocalDate.now());

            if (tokenValido) {
                usuario.setSenha(passwordEncoder.encode(novaSenha));
                usuario.setResetPasswordToken(null);
                usuario.setResetPasswordTokenExpiryDate(null);
                usuarioRepository.save(usuario);
                return true;
            }
        }
        return false;
    }
}

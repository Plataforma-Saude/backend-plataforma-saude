package plataformaSaude.service;

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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void salvarUsuario(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
    }
    public String hashSenha(String senha){
        return passwordEncoder.encode(senha);
    }


    public boolean validarSenha(Usuario usuario, String senhaDigitada) {
        return passwordEncoder.matches(senhaDigitada, usuario.getSenha());
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
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

    public boolean redefinirSenha(String token, String novaSenha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByResetPasswordToken(token);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getResetPasswordTokenExpiryDate().isAfter(LocalDate.now())) {
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
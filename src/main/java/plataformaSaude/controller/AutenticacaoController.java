package plataformaSaude.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
// Importações de DTOs
import plataformaSaude.dto.PasswordResetRequest;
import plataformaSaude.dto.PasswordResetConfirm;
import plataformaSaude.dto.LoginRequest;
import plataformaSaude.dto.AuthResponse;
// Importações de Modelos e Serviços
import plataformaSaude.model.Medico;
import plataformaSaude.model.Paciente;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;
import plataformaSaude.service.UsuarioService;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    private final UsuarioService usuarioService;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final JwtEncoder jwtEncoder;

    // 1. INJEÇÃO DE DEPENDÊNCIA VIA CONSTRUTOR
    public AutenticacaoController(
            UsuarioService usuarioService,
            MedicoRepository medicoRepository,
            PacienteRepository pacienteRepository,
            JwtEncoder jwtEncoder // Injetado do OAuth2Config
    ) {
        this.usuarioService = usuarioService;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.jwtEncoder = jwtEncoder;
    }

    // --- MÉTODOS DE REGISTRO ---

    @PostMapping("/register/paciente")
    public ResponseEntity<Paciente> registrarPaciente(@RequestBody Paciente paciente) {
        paciente.setTipoUsuario("PACIENTE"); //Permissão
        paciente.setSenha(usuarioService.hashSenha(paciente.getSenha()));
        Paciente novoPaciente = pacienteRepository.save(paciente);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPaciente);
    }

    @PostMapping("/register/medico")
    public ResponseEntity<Medico> registrarMedico(@RequestBody Medico medico) {
        medico.setTipoUsuario("MEDICO"); // Define a Role (ajustado no Usuario.java)
        medico.setSenha(usuarioService.hashSenha(medico.getSenha()));
        Medico novoMedico = medicoRepository.save(medico);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoMedico);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());
        if (usuario == null || !usuarioService.validarSenha(usuario, request.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Instant now = Instant.now();
        String role = usuario.getAuthorities().iterator().next().getAuthority();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .claim("roles", role)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new AuthResponse(token, "Login bem-sucedido"));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<String> solicitarResetSenha(@RequestBody PasswordResetRequest request) {
        String email = request.getEmail();
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("O e-mail é obrigatório.");
        }
        return ResponseEntity.ok("Caso o seu e-mail esteja cadastrado na nossa plataforma, você receberá um e-mail em instantes.");
    }

    @PostMapping("/reset-senha")
    public ResponseEntity<String> redefinirSenha(@RequestBody PasswordResetConfirm request) {
        String token = request.getToken();
        String novaSenha = request.getNovaSenha();

        if (token == null || token.isEmpty() || novaSenha == null || novaSenha.isEmpty()) {
            return ResponseEntity.badRequest().body("Token e nova senha são obrigatórios.");
        }
        if (usuarioService.redefinirSenha(token, novaSenha)) {
            return ResponseEntity.ok("Nova senha criada com sucesso!!");
        } else {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }
    }
}

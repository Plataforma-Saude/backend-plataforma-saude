package plataformaSaude.controller;

// Imports do HEAD
import jakarta.validation.Valid;
import plataformaSaude.dto.PacienteRegistroDTO;

// Imports da MAIN
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import plataformaSaude.dto.PasswordResetRequest;
import plataformaSaude.dto.PasswordResetConfirm;
import plataformaSaude.dto.LoginRequest;
import plataformaSaude.dto.AuthResponse;

// Imports comuns (mesclados)
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.model.Medico;
import plataformaSaude.model.Paciente;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;
import plataformaSaude.service.UsuarioService;

import java.time.Instant;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // Ajuste o CrossOrigin conforme necessário
public class AutenticacaoController {

    private final UsuarioService usuarioService;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final JwtEncoder jwtEncoder;

    // --- Injeção de dependência da MAIN ---
    public AutenticacaoController(
            UsuarioService usuarioService,
            MedicoRepository medicoRepository,
            PacienteRepository pacienteRepository,
            JwtEncoder jwtEncoder
    ) {
        this.usuarioService = usuarioService;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.jwtEncoder = jwtEncoder;
    }

    // --- Seu novo endpoint de registro de Paciente (do HEAD) ---
    @PostMapping("/register")
    public ResponseEntity<?> registrarPaciente(@Valid @RequestBody PacienteRegistroDTO dto) {
        if (usuarioService.buscarPorEmail(dto.getEmail()) != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erro: O email informado já está em uso");
        }

        if (usuarioService.buscarPorCpf(dto.getCpf()) != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erro: O CPF informado já está cadastrado.");
        }

        Paciente novoPaciente = new Paciente();
        novoPaciente.setEmail(dto.getEmail());
        novoPaciente.setCpf(dto.getCpf());

        String nomeCompleto = dto.getNomeCompleto();
        String[] partesNome = nomeCompleto.trim().split(" ", 2);

        novoPaciente.setNome(partesNome[0]);

        if (partesNome.length > 1) {
            novoPaciente.setSobrenome(partesNome[1]);
        } else {
            novoPaciente.setSobrenome("");
        }

        // --- CORREÇÃO DE SEGURANÇA (aplicada) ---
        novoPaciente.setSenha(usuarioService.hashSenha(dto.getSenha()));

        novoPaciente.setTipoUsuario("PACIENTE");

        try {
            usuarioService.salvarUsuario(novoPaciente);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Usuário registrado com sucesso");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    // --- Endpoint de registro de Médico (da MAIN) ---
    @PostMapping("/register/medico")
    public ResponseEntity<Medico> registrarMedico(@RequestBody Medico medico) {
        medico.setTipoUsuario("MEDICO"); // Define a Role
        medico.setSenha(usuarioService.hashSenha(medico.getSenha()));
        Medico novoMedico = medicoRepository.save(medico);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoMedico);
    }


    // --- Métodos de Login e Reset (da MAIN) ---

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
                .expiresAt(now.plusSeconds(3600)) // Token expira em 1 hora
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

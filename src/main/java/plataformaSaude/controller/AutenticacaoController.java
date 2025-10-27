package plataformaSaude.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.dto.*;
import plataformaSaude.model.Medico;
import plataformaSaude.model.Paciente;
import plataformaSaude.model.Usuario;
import plataformaSaude.model.RefreshToken;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;
import plataformaSaude.service.UsuarioService;
import plataformaSaude.service.RefreshTokenService;


import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AutenticacaoController {

    private final UsuarioService usuarioService;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final JwtEncoder jwtEncoder;
    private final RefreshTokenService refreshTokenService;

    public AutenticacaoController(
            UsuarioService usuarioService,
            MedicoRepository medicoRepository,
            PacienteRepository pacienteRepository,
            JwtEncoder jwtEncoder,
            RefreshTokenService refreshTokenService
    ) {
        this.usuarioService = usuarioService;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    // Registro de Paciente
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
        novoPaciente.setSobrenome(partesNome.length > 1 ? partesNome[1] : "");

        // 👉 Alterado: NÃO hash manual aqui
        novoPaciente.setSenha(dto.getSenha());
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

    // Registro de Médico
    @PostMapping("/register/medico")
    public ResponseEntity<Medico> registrarMedico(@RequestBody Medico medico) {
        medico.setTipoUsuario("MEDICO");
        medico.setSenha(medico.getSenha()); // encode ocorre no service
        Medico novoMedico = medicoRepository.save(medico);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoMedico);
    }

    // Login com geração de Access + Refresh Token
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
                .expiresAt(now.plusSeconds(3600)) // Access Token: 1h
                .subject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .claim("roles", role)
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // Criação do refresh token persistido no banco
        RefreshToken refreshToken = refreshTokenService.criarRefreshToken(usuario.getEmail());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        Optional<RefreshToken> validToken = refreshTokenService.validarRefreshToken(refreshToken);

        if (validToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token inválido ou expirado"));
        }

        String username = validToken.get().getUsername();
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(username)
                .build();

        String novoAccessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(Map.of(
                "accessToken", novoAccessToken,
                "refreshToken", refreshToken
        ));
    }

    //Redefinição de senha
    @PostMapping("/redefinir-senha")
    public ResponseEntity<String> solicitarResetSenha(@RequestBody PasswordResetRequest request) {
        String email = request.getEmail();
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("O e-mail é obrigatório.");
        }
        return ResponseEntity.ok("Caso o seu e-mail esteja cadastrado, você receberá um link em instantes.");
    }

    // Confirmar redefinição de senha
    @PostMapping("/reset-senha")
    public ResponseEntity<String> redefinirSenha(@RequestBody PasswordResetConfirm request) {
        String token = request.getToken();
        String novaSenha = request.getNovaSenha();

        if (token == null || token.isEmpty() || novaSenha == null || novaSenha.isEmpty()) {
            return ResponseEntity.badRequest().body("Token e nova senha são obrigatórios.");
        }

        if (usuarioService.redefinirSenha(token, novaSenha)) {
            return ResponseEntity.ok("Nova senha criada com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }
    }
}

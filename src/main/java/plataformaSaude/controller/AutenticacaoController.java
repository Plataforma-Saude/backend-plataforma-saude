package plataformaSaude.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plataformaSaude.service.UsuarioService;
import plataformaSaude.dto.PasswordResetRequest;
import plataformaSaude.dto.PasswordResetConfirm;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    @Autowired
    private UsuarioService usuarioService;

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
            return ResponseEntity.badRequest().body("Token inválido / expirado.");
        }
    }
}
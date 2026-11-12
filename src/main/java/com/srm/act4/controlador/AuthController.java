package com.srm.act4.controlador;

import com.srm.act4.dto.LoginDTO;
import com.srm.act4.dto.RegistroDTO;
import com.srm.act4.dto.TokenDTO;
import com.srm.act4.modelo.Usuario;
import com.srm.act4.repositorio.UsuarioRepository;
import com.srm.act4.seguridad.JwtService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final AuthenticationManager autenticacion;
    private final JwtService jwt;

    public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder,
                          AuthenticationManager autenticacion, JwtService jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.autenticacion = autenticacion;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenDTO> registrar(@Valid @RequestBody RegistroDTO datos) {
        String correo = datos.correo().trim().toLowerCase(Locale.ROOT);
        if (usuarios.existsByCorreo(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya esta registrado");
        }
        usuarios.save(new Usuario(correo, encoder.encode(datos.contrasena())));
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenDTO(jwt.generar(correo)));
    }

    @PostMapping("/login")
    public TokenDTO entrar(@Valid @RequestBody LoginDTO datos) {
        String correo = datos.correo().trim().toLowerCase(Locale.ROOT);
        autenticacion.authenticate(new UsernamePasswordAuthenticationToken(correo, datos.contrasena()));
        return new TokenDTO(jwt.generar(correo));
    }
}

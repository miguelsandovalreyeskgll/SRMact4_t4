package com.srm.act4.seguridad;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.srm.act4.repositorio.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SeguridadConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService usuarios(UsuarioRepository repositorio) {
        return correo -> repositorio.findByCorreo(correo)
                .map(usuario -> User.withUsername(usuario.getCorreo())
                        .password(usuario.getContrasena()).roles("USER").build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Bean
    public AuthenticationManager autenticacion(UserDetailsService usuarios, PasswordEncoder encoder) {
        DaoAuthenticationProvider proveedor = new DaoAuthenticationProvider(usuarios);
        proveedor.setPasswordEncoder(encoder);
        return new ProviderManager(proveedor);
    }

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reglas -> reglas
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .exceptionHandling(errores -> errores.authenticationEntryPoint((peticion, respuesta, e) -> {
                    respuesta.setStatus(401);
                    respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    respuesta.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    respuesta.getWriter().write("{\"error\":\"Token ausente o invalido\"}");
                }));
        return http.build();
    }

    @Bean
    public SecretKey clave(@Value("${jwt.secret}") String secreto) {
        byte[] bytes = Base64.getDecoder().decode(secreto);
        if (bytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 bytes en Base64");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey clave) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(clave));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey clave) {
        return NimbusJwtDecoder.withSecretKey(clave).macAlgorithm(MacAlgorithm.HS256).build();
    }
}

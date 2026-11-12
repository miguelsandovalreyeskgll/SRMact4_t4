package com.srm.act4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroDTO(
        @Email(message = "Correo invalido") @NotBlank String correo,
        @NotBlank @Size(min = 8, message = "Minimo 8 caracteres") String contrasena
) { }

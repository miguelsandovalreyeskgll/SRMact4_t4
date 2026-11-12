package com.srm.act4.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank String correo,
        @NotBlank String contrasena
) { }

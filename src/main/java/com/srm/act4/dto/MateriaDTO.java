package com.srm.act4.dto;

import jakarta.validation.constraints.NotBlank;

public record MateriaDTO(
        Long id,
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "La categoria es obligatoria") String categoria
) { }

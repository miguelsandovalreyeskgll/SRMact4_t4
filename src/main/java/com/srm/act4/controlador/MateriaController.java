package com.srm.act4.controlador;

import com.srm.act4.dto.MateriaDTO;
import com.srm.act4.servicio.MateriaService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/materias")
public class MateriaController {
    private final MateriaService servicio;

    public MateriaController(MateriaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public Map<String, Object> listar(@RequestParam(defaultValue = "0") int pagina,
                                      @RequestParam(defaultValue = "10") int tamanio) {
        return servicio.listar(pagina, tamanio);
    }

    @GetMapping("/{id}")
    public MateriaDTO obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    public ResponseEntity<MateriaDTO> crear(@Valid @RequestBody MateriaDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(datos));
    }

    @PutMapping("/{id}")
    public MateriaDTO actualizar(@PathVariable Long id, @Valid @RequestBody MateriaDTO datos) {
        return servicio.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

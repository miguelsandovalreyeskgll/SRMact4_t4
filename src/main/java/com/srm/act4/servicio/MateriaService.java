package com.srm.act4.servicio;

import com.srm.act4.dto.MateriaDTO;
import com.srm.act4.modelo.Materia;
import com.srm.act4.repositorio.MateriaRepository;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MateriaService {
    private final MateriaRepository repositorio;

    public MateriaService(MateriaRepository repositorio) {
        this.repositorio = repositorio;
    }

    private Materia buscar(Long id) {
        return repositorio.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada"));
    }

    private MateriaDTO convertir(Materia materia) {
        return new MateriaDTO(materia.getId(), materia.getNombre(), materia.getCategoria());
    }

    public Map<String, Object> listar(int pagina, int tamanio) {
        if (pagina < 0 || tamanio < 1 || tamanio > 50) {
            throw new IllegalArgumentException("Pagina y tamanio invalidos");
        }
        Page<MateriaDTO> datos = repositorio.findAll(PageRequest.of(pagina, tamanio)).map(this::convertir);
        return Map.of("contenido", datos.getContent(), "pagina", datos.getNumber(),
                "total", datos.getTotalElements(), "paginas", datos.getTotalPages());
    }

    public MateriaDTO obtener(Long id) {
        return convertir(buscar(id));
    }

    public MateriaDTO crear(MateriaDTO datos) {
        Materia materia = new Materia();
        materia.setNombre(datos.nombre());
        materia.setCategoria(datos.categoria());
        return convertir(repositorio.save(materia));
    }

    public MateriaDTO actualizar(Long id, MateriaDTO datos) {
        Materia materia = buscar(id);
        materia.setNombre(datos.nombre());
        materia.setCategoria(datos.categoria());
        return convertir(repositorio.save(materia));
    }

    public void eliminar(Long id) {
        repositorio.delete(buscar(id));
    }
}

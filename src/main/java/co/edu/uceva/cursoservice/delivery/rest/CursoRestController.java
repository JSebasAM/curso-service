package co.edu.uceva.cursoservice.delivery.rest;

import co.edu.uceva.cursoservice.domain.exception.CursoNoEncontradoException;
import co.edu.uceva.cursoservice.domain.exception.NoHayCursosException;
import co.edu.uceva.cursoservice.domain.exception.PaginasSinCursos;
import co.edu.uceva.cursoservice.domain.exception.ValidationException;
import co.edu.uceva.cursoservice.domain.model.Curso;
import co.edu.uceva.cursoservice.domain.service.ICursoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/curso-service")
public class CursoRestController {

    private final ICursoService cursoService;

    private static final String ERROR = "error";
    private static final String MENSAJE = "mensaje";
    private static final String CURSO = "facultad";
    private static final String CURSOS = "facultades";


    public CursoRestController(ICursoService cursoService) {
        this.cursoService = cursoService;
    }

    /**
     * LISTAR TODOS LOS CURSOS
     */
    @GetMapping("/cursos")
    public ResponseEntity<Map<String, Object>> getCursos() {

        List<Curso> cursos = cursoService.findAll();

        if (cursos.isEmpty()) {
            throw new NoHayCursosException();
        }
        Map<String, Object> response = new HashMap<>();
        response.put(CURSOS, cursos);
        return ResponseEntity.ok(response);
    }

    /**
     * LISTAR CON PAGINACIÓN
     */
    @GetMapping("/cursos/page/{page}")
    public ResponseEntity<Object> index(@PathVariable Integer page) {
        Pageable pageable = PageRequest.of(page, 4);
        Page<Curso> cursos = cursoService.findAll(pageable);

        if (cursos.isEmpty()) {
            throw new PaginasSinCursos(page);
        }
        return ResponseEntity.ok(cursos);
    }

    /**
     * CREAR UN NUEVO CURSO PASANDO EL OBJETO EN EL CUERPO DE LA PETICIÓN
     **/
    @PostMapping("/cursos")
    public ResponseEntity<Map<String, Object>> save(@RequestBody Curso facultad, BindingResult result) {

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
        Map<String, Object> response = new HashMap<>();
        Curso nuevoCurso = cursoService.save(facultad);
        response.put(MENSAJE, "El curso se ha registrado correctamente");
        response.put(CURSO, nuevoCurso);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * EMIMINAR UN CURSO PASANDO EL OBJETO EN EL CUERPO DE LA PETICIÓN
     **/
    @DeleteMapping("/cursos")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Long id) {

        cursoService.findById(id).orElseThrow(() -> new CursoNoEncontradoException(id));
        cursoService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put(MENSAJE, "La facultad se ha eliminado correctamente");
        response.put(CURSO, null);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar un curso pasando el objeto en el cuerpo de la petición.
     *
     * @param curso: Objeto Curso que se va a actualizar
     */
    @PutMapping("/cursos")
    public ResponseEntity<Map<String, Object>> update(@RequestBody Curso curso, BindingResult result) {
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
        cursoService.findById(curso.getId())
                .orElseThrow(() -> new CursoNoEncontradoException(curso.getId()));

        Map<String, Object> response = new HashMap<>();
        Curso facultadActualizada = cursoService.update(curso);

        response.put(MENSAJE, "La facultad se ha actualizado correctamente");
        response.put(CURSO, facultadActualizada);
        return ResponseEntity.ok(response);
    }

    /**
     * OBTENER EL CURSO POR SU ID
     **/
    @GetMapping("/cursos/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable("id") Long id) {

        Curso facultad = cursoService.findById(id).
                orElseThrow(() -> new CursoNoEncontradoException(id));
        Map<String, Object> response = new HashMap<>();
        response.put(MENSAJE, "La facultad se ha obtenido correctamente");
        response.put(CURSO, facultad);
        return ResponseEntity.ok(response);
    }
}

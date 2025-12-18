package ar.org.adriel_medero.java.pickyourmon.controller;

import ar.org.adriel_medero.java.pickyourmon.model.Categoria;
import ar.org.adriel_medero.java.pickyourmon.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // esta clase maneja peticiones web y devuelve JSON
@RequestMapping("/api/categorias") // URL base
@CrossOrigin(origins = "*") // frontend interactúa con este backend
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping // Responde a peticiones GET
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @GetMapping("/{id}") // Responde a GET /api/categorias/1
    public ResponseEntity<Categoria> obtenerPorId(@PathVariable Long id) {
        return categoriaService.obtenerPorId(id)
                .map(ResponseEntity::ok) // si existe, devuelve 200 OK
                .orElse(ResponseEntity.notFound().build()); // si no, 404 Not Found
    }
}
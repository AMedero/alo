package ar.org.adriel_medero.java.pickyourmon.service;

import ar.org.adriel_medero.java.pickyourmon.model.Categoria;
import ar.org.adriel_medero.java.pickyourmon.repository.ICategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private ICategoriaRepository categoriaRepository;

    // todas las categorías
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    // filtrar por alguna categoría específica
    public Optional<Categoria> obtenerPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    // crear o actualizar (Solo Admin)
    public void guardar(Categoria categoria) {
        categoriaRepository.save(categoria);
    }
    
    // eliminar categoría (Solo Admin)
    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }
}
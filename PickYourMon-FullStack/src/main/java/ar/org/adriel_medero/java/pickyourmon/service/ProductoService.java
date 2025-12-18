package ar.org.adriel_medero.java.pickyourmon.service;

import ar.org.adriel_medero.java.pickyourmon.model.Producto;
import ar.org.adriel_medero.java.pickyourmon.repository.IProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service // informa a spring que esta clase tiene lógica de negocio
public class ProductoService {

    @Autowired // spring: genera una instancia de IProductoRepository
    private IProductoRepository productoRepository;

    // -------------------------------- MÉTODOS DE LECTURA --------------------------------

    // devuleve todos los productos
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // trae un peluche por id (para ver el detalle)
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    // busca por categoría (para búsquedas con filtro: "solo ver categoría X" -ej: solo fuego-)
    public List<Producto> filtrarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    // busca por nombre (barra de búsqueda)
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // -------------------------------- MÉTODOS DE ESCRITURA (ADMIN) --------------------------------

    // guardar o actualizar un producto
    // (si el producto tiene id, actualiza. Si no tiene, crea uno nuevo)
    public void guardarProducto(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        //  con BigDecimal se usa compareTo para comparar valores
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo ni nulo");
        }
        if (producto.getStock() != null && producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        productoRepository.save(producto);
    }

    public void borrarProducto(Long id) {
        productoRepository.deleteById(id);
    }
}
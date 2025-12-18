package ar.org.adriel_medero.java.pickyourmon.controller;

import ar.org.adriel_medero.java.pickyourmon.dto.ProductoDTO;
import ar.org.adriel_medero.java.pickyourmon.model.Producto;
import ar.org.adriel_medero.java.pickyourmon.service.CategoriaService;
import ar.org.adriel_medero.java.pickyourmon.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    // -------------------------------- ENDPOINTS --------------------------------

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar() {
        // buscamos todos los productos (entidades)
        List<Producto> productos = productoService.listarProductos();
        
        // las convertimos a DTOs con el mapper manual
        List<ProductoDTO> dtos = productos.stream()
                .map(this::convertirADTO) // convierte cada Producto a ProductoDTO
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(this::convertirADTO) // Si lo encuentra, lo convierte
                .map(ResponseEntity::ok)  // Y lo devuelve con OK
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> crear(@RequestBody ProductoDTO dto) {
        // 1. Convertimos el JSON (DTO) a un objeto real (Entidad)
        Producto productoNuevo = convertirAEntidad(dto);
        
        // 2. Lo guardamos en la base de datos
        productoService.guardarProducto(productoNuevo);
        
        // 3. Respondemos 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productoService.obtenerPorId(id).isPresent()) {
            productoService.borrarProducto(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // -------------------------------- MAPPER MANUAL (TRADUCTOR) --------------------------------
    
    // convierte entidad (DB) a DTO (JSON limpio)
    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setImagenUrl(producto.getImagenUrl());
        
        // mandamos solo el nombre de la categoría y no todo el objeto
        if (producto.getCategoria() != null) {
            dto.setNombreCategoria(producto.getCategoria().getNombre());
            dto.setCategoriaId(producto.getCategoria().getId()); // se agrega el ID de la categoría por si es necesario
        }
        return dto;
    }

    // convierte DTO (JSON) a entidad (DB)
    private Producto convertirAEntidad(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setImagenUrl(dto.getImagenUrl());

        // Buscamos la categoría real usando el servicio
        if (dto.getCategoriaId() != null) {
            categoriaService.obtenerPorId(dto.getCategoriaId())
                    .ifPresent(producto::setCategoria);
        }
        return producto;
    }
}
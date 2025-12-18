package ar.org.adriel_medero.java.pickyourmon.controller;

import ar.org.adriel_medero.java.pickyourmon.dto.CompraDTO;
import ar.org.adriel_medero.java.pickyourmon.dto.DetalleCompraDTO;
import ar.org.adriel_medero.java.pickyourmon.model.DetalleOrden;
import ar.org.adriel_medero.java.pickyourmon.model.Orden;
import ar.org.adriel_medero.java.pickyourmon.model.Producto;
import ar.org.adriel_medero.java.pickyourmon.model.Usuario;
import ar.org.adriel_medero.java.pickyourmon.service.OrdenService;
import ar.org.adriel_medero.java.pickyourmon.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/comprar")
    public ResponseEntity<?> crearOrden(@RequestBody CompraDTO compraDTO) {
        try {
            // busqueda del usuario que hace la compra
            Usuario usuario = usuarioService.buscarPorId(compraDTO.getUsuarioId())
                    .orElse(null);

            if (usuario == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            // lista de detalles de la orden para que el service la procese
            List<DetalleOrden> detalles = new ArrayList<>();

            for (DetalleCompraDTO item : compraDTO.getItems()) {
                // Solo necesitamos el ID del producto y la cantidad
                // El service se encarga de buscar el precio real y descontar stock
                Producto productoMock = new Producto();
                productoMock.setId(item.getProductoId());

                DetalleOrden detalle = new DetalleOrden();
                detalle.setProducto(productoMock);
                detalle.setCantidad(item.getCantidad());

                detalles.add(detalle);
            }

            // service se encarga de todo el proceso de compra (validaciones, stock, total,
            // etc)
            Orden ordenCreada = ordenService.crearOrden(usuario, detalles);

            // se devuelve 201 Created con un mensaje simple
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Orden creada con éxito. ID: " + ordenCreada.getId() + " - Total: $"
                            + ordenCreada.getTotal());

        } catch (RuntimeException e) {
            // si falla por falta de stock o producto no encontrado (errores del service)
            return ResponseEntity.badRequest().body("Error al procesar la compra: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error inesperado: " + e.getMessage());
        }
    }

    // GET: Ver historial de compras de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Orden>> obtenerOrdenesPorUsuario(@PathVariable Long usuarioId) {
        // Llamamos al método de lectura del servicio
        List<Orden> ordenes = ordenService.listarOrdenesDeUsuario(usuarioId);
        return ResponseEntity.ok(ordenes);
    }
}
package ar.org.adriel_medero.java.pickyourmon.service;

import ar.org.adriel_medero.java.pickyourmon.model.*;
import ar.org.adriel_medero.java.pickyourmon.model.enums.EstadoOrden;
import ar.org.adriel_medero.java.pickyourmon.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrdenService {

    @Autowired
    private IOrdenRepository ordenRepository;

    @Autowired
    private IDetalleOrdenRepository detalleOrdenRepository;

    @Autowired
    private IProductoRepository productoRepository;

    // -------------------------------- LECTURA --------------------------------

    //  lectura es decir "obtener datos" (GET)
    public List<Orden> listarOrdenesDeUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    // -------------------------------- ESCRITURA --------------------------------

    // @Transactional lo que hace es queasegura que si algo falla en el medio (ej: falta stock de un ítem),
    // se hace rollback de toda la operación, dejando la base de datos como estaba antes de empezar
    @Transactional
    public Orden crearOrden(Usuario usuario, List<DetalleOrden> detalles) {
        // creo la cabecera de la orden
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setEstado(EstadoOrden.PENDIENTE);
        orden.setTotal(BigDecimal.ZERO); // Lo calculamos abajo

        // se gurada la orden para obtener su id y poder usarla en los detalles
        orden = ordenRepository.save(orden);

        BigDecimal totalCalculado = BigDecimal.ZERO;

        // para cada detalle de la orden
        for (DetalleOrden detalle : detalles) {
            // se obtiene el producto real desde la base de datos
            Producto productoReal = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalle.getProducto().getId()));

            // se valida el stock
            if (productoReal.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("No hay stock suficiente para: " + productoReal.getNombre());
            }

            // se descuenta el stock
            productoReal.setStock(productoReal.getStock() - detalle.getCantidad());
            productoRepository.save(productoReal);

            // se completa el detalle
            detalle.setOrden(orden);
            detalle.setProducto(productoReal);
            detalle.setPrecioUnitario(productoReal.getPrecio()); // se fija el precio unitario al momento de la orden

            // suma total final
            // total = total + (precio * cantidad)
            BigDecimal subtotal = productoReal.getPrecio().multiply(new BigDecimal(detalle.getCantidad()));
            totalCalculado = totalCalculado.add(subtotal);

            // se guarda el detalle
            detalleOrdenRepository.save(detalle);
        }

        // finalizamos la orden con el total calculado
        orden.setTotal(totalCalculado);
        return ordenRepository.save(orden);
    }
}
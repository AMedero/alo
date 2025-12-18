package ar.org.adriel_medero.java.pickyourmon.model;

import ar.org.adriel_medero.java.pickyourmon.model.enums.EstadoOrden;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now(); // fecha actual de creación de la orden

    private BigDecimal total;

    // muchas órdenes pertenecen a un usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // estado de la orden (PENDIENTE, PAGADO, ENVIADO, CANCELADO)
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 50)
    private EstadoOrden estado = EstadoOrden.PENDIENTE;

    // datos futuros para MercadoPago
    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "id_transaccion_mp")
    private String idTransaccionMp;
}
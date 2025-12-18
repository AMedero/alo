package ar.org.adriel_medero.java.pickyourmon.model.enums;

public enum EstadoOrden {
    PENDIENTE,  // En el carrito
    PAGADO,     // Volvió de MercadoPago OK
    ENVIADO,    // Ya salió el paquete
    CANCELADO   // Hubo un problema
}
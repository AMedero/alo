package ar.org.adriel_medero.java.pickyourmon.dto;

import lombok.Data;
import java.util.List;

@Data
public class CompraDTO {
    private Long usuarioId; // el cliente que realiza la compra
    private List<DetalleCompraDTO> items; // La lista de productos y cantidades
}
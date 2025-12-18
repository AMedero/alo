package ar.org.adriel_medero.java.pickyourmon.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private Integer stock;
    private Long categoriaId; // opcional, si se quiere enviar el id de la categoría
    private String nombreCategoria; // devuelve solo el nombre de la categoría
}
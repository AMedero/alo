package ar.org.adriel_medero.java.pickyourmon.dto;
import lombok.Data;

@Data
public class DetalleOrdenDTO {
    private Long idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private Double precio;
}
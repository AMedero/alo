package ar.org.adriel_medero.java.pickyourmon.dto;
import lombok.Data;
import java.util.List;

@Data
public class OrdenDTO {
    private Long idUsuario;
    private Double total;
    private List<DetalleOrdenDTO> detalles;
}
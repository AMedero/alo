package ar.org.adriel_medero.java.pickyourmon.dto;

import ar.org.adriel_medero.java.pickyourmon.model.enums.Rol;
import lombok.Data;
import java.time.LocalDate;

@Data
// no se incluye el campo contraseña por seguridad
public class UsuarioDTO {
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String dni;
    private LocalDate fechaNacimiento;
    private String direccion;
    private Rol rol;
}

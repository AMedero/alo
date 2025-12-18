package ar.org.adriel_medero.java.pickyourmon.model;

import ar.org.adriel_medero.java.pickyourmon.model.enums.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // VALIDACIÓN DE EMAIL
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un formato de email válido") // debe tener @ y .com
    @Column(unique = true, nullable = false)
    private String email;

    // VALIDACIÓN DE PASSWORD
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    // DATOS PERSONALES OBLIGATORIOS
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    // VALIDACIÓN DE DNI
    @NotBlank(message = "El DNI no puede estar vacío")
    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe contener exactamente 8 números y sin letras")
    private String dni;
    
    // VALIDACIÓN DE FECHA DE NACIMIENTO
    @NotNull(message = "La fecha de nacimiento es obligatoria") // para objetos (Date/LocalDate) se usa NotNull
    @Past(message = "La fecha de nacimiento debe ser en el pasado") // no podes nacer en el futuro
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "La dirección es obligatoria para el envío")
    private String direccion;

    // ROL
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Rol rol = Rol.USER; // por defecto es usuario común
}
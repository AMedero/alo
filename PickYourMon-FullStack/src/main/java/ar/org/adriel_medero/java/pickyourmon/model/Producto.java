package ar.org.adriel_medero.java.pickyourmon.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(name = "imagen_url")
    private String imagenUrl;

    private Integer stock;

    // relación con categoria (muchos productos pueden pertenecer a una categoria)
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
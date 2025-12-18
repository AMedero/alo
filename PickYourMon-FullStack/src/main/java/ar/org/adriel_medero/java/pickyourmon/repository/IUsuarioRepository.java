package ar.org.adriel_medero.java.pickyourmon.repository;

import ar.org.adriel_medero.java.pickyourmon.model.Usuario;
import ar.org.adriel_medero.java.pickyourmon.model.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    // login y registro
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);

    // búsquedas parciales
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    List<Usuario> findByApellidoContainingIgnoreCase(String apellido);

    // búsqueda exacta por DNI
    Optional<Usuario> findByDni(String dni);

    // filtro por rol
    List<Usuario> findByRol(Rol rol);
}
package ar.org.adriel_medero.java.pickyourmon.repository;

import ar.org.adriel_medero.java.pickyourmon.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuarioId(Long usuarioId); // Historial de compras
}
package ar.org.adriel_medero.java.pickyourmon.repository;

import ar.org.adriel_medero.java.pickyourmon.model.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
}
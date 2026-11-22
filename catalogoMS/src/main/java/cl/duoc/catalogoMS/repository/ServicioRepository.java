package cl.duoc.catalogoMS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.catalogoMS.model.Servicio;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

    Optional<Servicio> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

}

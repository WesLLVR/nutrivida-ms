package cl.duoc.ventaMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.ventaMS.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByPacienteIdOrderByNumeroDesc(Integer pacienteId);

}

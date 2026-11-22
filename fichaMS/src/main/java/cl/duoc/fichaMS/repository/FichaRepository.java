package cl.duoc.fichaMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.fichaMS.model.Ficha;

@Repository
public interface FichaRepository extends JpaRepository<Ficha, Integer> {

    List<Ficha> findByPacienteIdOrderByFechaDesc(Integer pacienteId);

    List<Ficha> findByNutricionistaIdOrderByFechaDesc(Integer nutricionistaId);

}

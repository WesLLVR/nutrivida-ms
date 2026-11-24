package cl.duoc.citaMS.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.citaMS.model.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    List<Cita> findByPacienteIdOrderByFechaAscHoraAsc(Integer pacienteId);

    List<Cita> findByNutricionistaIdOrderByFechaAscHoraAsc(Integer nutricionistaId);

    List<Cita> findByNutricionistaIdAndFechaAndEstadoNot(Integer nutricionistaId, LocalDate fecha, String estado);

    boolean existsByNutricionistaIdAndFechaAndHoraAndEstadoNot(Integer nutricionistaId, LocalDate fecha,
            String hora, String estado);

}

package cl.duoc.citaMS.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.citaMS.model.Cita;
import cl.duoc.citaMS.repository.CitaRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(CitaRepository citaRepository) {
        return args -> {
            if (citaRepository.count() == 0) {
                LocalDate hoy = LocalDate.now();
                citaRepository.save(new Cita(null, 2, 1, hoy.plusDays(3), "10:00",
                        "Primera consulta para bajar de peso", "CONFIRMADA"));
                citaRepository.save(new Cita(null, 2, 1, hoy.plusDays(17), "10:30",
                        "Control quincenal", "PENDIENTE"));
                System.out.println("Datos de citas cargados");
            } else {
                System.out.println("Datos de citas ya existen");
            }
        };
    }
}

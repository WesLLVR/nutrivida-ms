package cl.duoc.fichaMS.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.fichaMS.model.Ficha;
import cl.duoc.fichaMS.repository.FichaRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(FichaRepository fichaRepository) {
        return args -> {
            if (fichaRepository.count() == 0) {
                LocalDate hoy = LocalDate.now();
                fichaRepository.save(new Ficha(null, 2, 1, hoy.minusDays(30), 92.0, 1.78, 101.0, 29.0,
                        "Primera evaluación. Se acuerda meta de bajar 4 kg en dos meses.",
                        "Desayuno: avena con fruta. Almuerzo: media porción de proteína, ensalada y legumbres. Once: yogur natural. Cena liviana."));
                fichaRepository.save(new Ficha(null, 2, 1, hoy.minusDays(15), 90.4, 1.78, 99.0, 28.5,
                        "Buena adherencia al plan. Se aumenta la actividad física a 3 veces por semana.",
                        "Se mantiene el plan anterior y se agregan dos colaciones con fruta o frutos secos."));
                System.out.println("Datos de fichas cargados");
            } else {
                System.out.println("Datos de fichas ya existen");
            }
        };
    }
}

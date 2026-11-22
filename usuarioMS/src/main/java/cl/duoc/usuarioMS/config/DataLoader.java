package cl.duoc.usuarioMS.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.usuarioMS.dto.UsuarioFormDTO;
import cl.duoc.usuarioMS.model.Comuna;
import cl.duoc.usuarioMS.model.Region;
import cl.duoc.usuarioMS.model.Rol;
import cl.duoc.usuarioMS.repository.ComunaRepository;
import cl.duoc.usuarioMS.repository.RegionRepository;
import cl.duoc.usuarioMS.repository.RolRepository;
import cl.duoc.usuarioMS.repository.UsuarioRepository;
import cl.duoc.usuarioMS.service.UsuarioService;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(RolRepository rolRepository, RegionRepository regionRepository,
            ComunaRepository comunaRepository, UsuarioRepository usuarioRepository,
            UsuarioService usuarioService) {
        return args -> {
            if (rolRepository.count() == 0) {
                rolRepository.save(new Rol(null, "ADMINISTRADOR"));
                rolRepository.save(new Rol(null, "NUTRICIONISTA"));
                rolRepository.save(new Rol(null, "PACIENTE"));
            }

            if (regionRepository.count() == 0) {
                guardarRegion(regionRepository, comunaRepository, "Valparaíso",
                        "Valparaíso", "Viña del Mar", "Quilpué");
                guardarRegion(regionRepository, comunaRepository, "Metropolitana",
                        "Santiago", "Providencia", "Maipú", "Puente Alto");
                guardarRegion(regionRepository, comunaRepository, "Biobío",
                        "Concepción", "Talcahuano", "Los Ángeles");
                guardarRegion(regionRepository, comunaRepository, "La Araucanía",
                        "Temuco", "Padre Las Casas", "Villarrica", "Pucón");
                guardarRegion(regionRepository, comunaRepository, "Los Lagos",
                        "Puerto Montt", "Osorno", "Castro");
            }

            if (usuarioRepository.count() == 0) {
                usuarioService.crear(new UsuarioFormDTO("212345679", "Javiera", "Contreras Núñez",
                        "admin@nutrivida.cl", "1234", "912345678", "Temuco", "Manuel Montt 210", "ADMINISTRADOR"));
                usuarioService.crear(new UsuarioFormDTO("198765430", "Matías", "Vergara Soto",
                        "usuario@correo.cl", "1234", "987654321", "Villarrica", "Los Robles 145", "PACIENTE"));
                usuarioService.crear(new UsuarioFormDTO("156789012", "Carolina", "Fuentes Mella",
                        "carolina.fuentes@nutrivida.cl", "1234", "911111111", "Temuco", "Av. Alemania 0450", "NUTRICIONISTA"));
                usuarioService.crear(new UsuarioFormDTO("167890124", "Rodrigo", "Sepúlveda Arce",
                        "rodrigo.sepulveda@nutrivida.cl", "1234", "922222222", "Temuco", "Av. Alemania 0450", "NUTRICIONISTA"));
                usuarioService.crear(new UsuarioFormDTO("178901236", "Daniela", "Morales Castro",
                        "daniela.morales@nutrivida.cl", "1234", "933333333", "Temuco", "Av. Alemania 0450", "NUTRICIONISTA"));
                usuarioService.crear(new UsuarioFormDTO("189012348", "Felipe", "Araya Rojas",
                        "felipe.araya@nutrivida.cl", "1234", "944444444", "Temuco", "Av. Alemania 0450", "NUTRICIONISTA"));
                System.out.println("Datos de usuarios cargados");
            } else {
                System.out.println("Datos de usuarios ya existen");
            }
        };
    }

    private void guardarRegion(RegionRepository regionRepository, ComunaRepository comunaRepository,
            String nombreRegion, String... comunas) {
        Region region = regionRepository.save(new Region(null, nombreRegion));
        for (String comuna : comunas) {
            comunaRepository.save(new Comuna(null, comuna, region));
        }
    }
}

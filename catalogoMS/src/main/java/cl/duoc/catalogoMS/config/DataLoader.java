package cl.duoc.catalogoMS.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.catalogoMS.model.Nutricionista;
import cl.duoc.catalogoMS.model.Servicio;
import cl.duoc.catalogoMS.model.TipoServicio;
import cl.duoc.catalogoMS.repository.NutricionistaRepository;
import cl.duoc.catalogoMS.repository.ServicioRepository;
import cl.duoc.catalogoMS.repository.TipoServicioRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(TipoServicioRepository tipoRepository, ServicioRepository servicioRepository,
            NutricionistaRepository nutricionistaRepository) {
        return args -> {
            if (tipoRepository.count() == 0) {
                TipoServicio consulta = tipoRepository.save(new TipoServicio(null, "Consulta"));
                TipoServicio plan = tipoRepository.save(new TipoServicio(null, "Plan"));
                TipoServicio evaluacion = tipoRepository.save(new TipoServicio(null, "Evaluación"));
                TipoServicio taller = tipoRepository.save(new TipoServicio(null, "Taller"));

                String imgConsulta = "/img/servicio-consulta.png";
                String imgPlan = "/img/servicio-plan.png";
                String imgEvaluacion = "/img/servicio-evaluacion.png";
                String imgTaller = "/img/servicio-taller.png";

                servicioRepository.save(new Servicio(null, "CN001", "Primera consulta nutricional", "50 min", "Presencial", 35000,
                        "Evaluación inicial: anamnesis, antropometría completa y diseño del primer plan alimenticio.", imgConsulta, true, consulta));
                servicioRepository.save(new Servicio(null, "CN002", "Control nutricional", "30 min", "Presencial", 25000,
                        "Seguimiento mensual: medición de indicadores y ajuste del plan vigente.", imgConsulta, false, consulta));
                servicioRepository.save(new Servicio(null, "CN003", "Control nutricional quincenal", "30 min", "Presencial", 22000,
                        "Seguimiento intensivo cada 15 días. Recomendado en los primeros 2 meses.", imgConsulta, false, consulta));
                servicioRepository.save(new Servicio(null, "CN004", "Teleconsulta nutricional", "30 min", "Online", 20000,
                        "Consulta de seguimiento por videollamada. Requiere una consulta presencial previa.", imgConsulta, false, consulta));
                servicioRepository.save(new Servicio(null, "CN005", "Consulta de urgencia", "30 min", "Presencial", 28000,
                        "Para pacientes que necesitan atención fuera de su control habitual.", imgConsulta, false, consulta));
                servicioRepository.save(new Servicio(null, "PL001", "Plan pérdida de peso (1 mes)", "1 mes", "Presencial", 65000,
                        "Primera consulta, un control quincenal, plan personalizado y seguimiento entre sesiones.", imgPlan, true, plan));
                servicioRepository.save(new Servicio(null, "PL002", "Plan pérdida de peso (3 meses)", "3 meses", "Presencial", 170000,
                        "Primera consulta, 5 controles, 3 planes mensuales y seguimiento continuo.", imgPlan, false, plan));
                servicioRepository.save(new Servicio(null, "PL003", "Plan nutrición deportiva", "1 mes", "Presencial", 70000,
                        "Para quienes entrenan seguido. Incluye cálculo de requerimientos energéticos y proteicos.", imgPlan, false, plan));
                servicioRepository.save(new Servicio(null, "PL004", "Plan control diabetes e hipertensión", "1 mes", "Presencial", 75000,
                        "Plan adaptado para enfermedades metabólicas, coordinado con el médico tratante.", imgPlan, false, plan));
                servicioRepository.save(new Servicio(null, "PL005", "Plan vegetariano o vegano", "1 mes", "Presencial", 68000,
                        "Asegura un buen aporte de proteínas, hierro, vitamina B12 y calcio sin productos animales.", imgPlan, false, plan));
                servicioRepository.save(new Servicio(null, "PL006", "Plan alimentación infantil", "1 mes", "Presencial", 65000,
                        "Evaluación nutricional para niños de 2 a 12 años y plan según su etapa de desarrollo.", imgPlan, false, plan));
                servicioRepository.save(new Servicio(null, "EV001", "Antropometría completa", "20 min", "Presencial", 18000,
                        "Peso, talla, IMC, circunferencias y porcentaje de grasa corporal.", imgEvaluacion, false, evaluacion));
                servicioRepository.save(new Servicio(null, "EV002", "Bioimpedanciometría", "15 min", "Presencial", 12000,
                        "Composición corporal: masa grasa, masa muscular, agua corporal y edad metabólica.", imgEvaluacion, false, evaluacion));
                servicioRepository.save(new Servicio(null, "EV003", "Encuesta de hábitos alimentarios", "20 min", "Presencial", 10000,
                        "Análisis de la alimentación actual para encontrar déficits y excesos.", imgEvaluacion, false, evaluacion));
                servicioRepository.save(new Servicio(null, "EV004", "Análisis de exámenes de laboratorio", "20 min", "Presencial", 15000,
                        "Interpretación de hemograma y perfil bioquímico y lipídico.", imgEvaluacion, false, evaluacion));
                servicioRepository.save(new Servicio(null, "TG001", "Taller de alimentación saludable", "90 min", "Grupal", 15000,
                        "Alimentación equilibrada y lectura de etiquetas. Máximo 10 personas.", imgTaller, true, taller));
                servicioRepository.save(new Servicio(null, "TG002", "Taller de cocina nutritiva", "120 min", "Grupal", 20000,
                        "Preparación de recetas saludables con degustación. Máximo 8 personas.", imgTaller, false, taller));
                servicioRepository.save(new Servicio(null, "TG003", "Taller nutrición para deportistas", "90 min", "Grupal", 18000,
                        "Hidratación y alimentación antes y después de entrenar. Máximo 12 personas.", imgTaller, false, taller));

                System.out.println("Datos del catálogo cargados");
            } else {
                System.out.println("Datos del catálogo ya existen");
            }

            if (nutricionistaRepository.count() == 0) {
                nutricionistaRepository.save(new Nutricionista(null, "NUT001", "Carolina Fuentes",
                        "Obesidad y síndrome metabólico", "Lunes, miércoles y viernes", "09:00 a 17:00",
                        "carolina.fuentes@nutrivida.cl"));
                nutricionistaRepository.save(new Nutricionista(null, "NUT002", "Rodrigo Sepúlveda",
                        "Nutrición deportiva", "Martes, jueves y sábado", "09:00 a 14:00",
                        "rodrigo.sepulveda@nutrivida.cl"));
                nutricionistaRepository.save(new Nutricionista(null, "NUT003", "Daniela Morales",
                        "Alimentación vegetariana y vegana", "Lunes a viernes", "08:00 a 13:00",
                        "daniela.morales@nutrivida.cl"));
                nutricionistaRepository.save(new Nutricionista(null, "NUT004", "Felipe Araya",
                        "Nutrición pediátrica y familiar", "Martes a viernes", "14:00 a 19:00",
                        "felipe.araya@nutrivida.cl"));
            }
        };
    }
}

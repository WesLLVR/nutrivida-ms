package cl.duoc.citaMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CitaMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CitaMsApplication.class, args);
    }

}

package cl.duoc.ventaMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class VentaMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(VentaMsApplication.class, args);
    }

}

package cl.duoc.fichaMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FichaMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FichaMsApplication.class, args);
    }

}

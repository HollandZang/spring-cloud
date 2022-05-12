package fml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class FmlApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmlApplication.class, args);
    }

}

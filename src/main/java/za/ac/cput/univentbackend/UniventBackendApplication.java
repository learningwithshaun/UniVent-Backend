package za.ac.cput.univentbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "za.ac.cput")
@AutoConfigurationPackage(basePackages = "za.ac.cput")
@EnableJpaRepositories("za.ac.cput.repository")
public class UniventBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniventBackendApplication.class, args);
    }

}

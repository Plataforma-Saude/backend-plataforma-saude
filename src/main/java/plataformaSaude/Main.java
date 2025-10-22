package plataformaSaude;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"plataformaSaude.model", "plataformaSaude.entity"})
@EnableJpaRepositories(basePackages = "plataformaSaude.repository")
@ComponentScan(basePackages = "plataformaSaude")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("🚀 Aplicação iniciada com sucesso!");
    }
}

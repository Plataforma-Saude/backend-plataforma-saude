package estruturaDoProjeto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjetoBase {
    public static void main(String[] args) {
        SpringApplication.run(ProjetoBase.class, args);
        System.out.println("Hello World");
    }
}

package estruturaDoProjeto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Testando {

    @GetMapping("/Testando")
    public String hello() {
        return "Hello World!";
    }
}
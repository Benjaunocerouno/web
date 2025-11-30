package lp2.fisi.web;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "lp2.fisi.web")
public class WebApplication {
    public static void main(String[] args) {SpringApplication.run(WebApplication.class, args);
    }
}
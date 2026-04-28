package wlsh.project.intervai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class IntervaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntervaiApplication.class, args);
    }

}

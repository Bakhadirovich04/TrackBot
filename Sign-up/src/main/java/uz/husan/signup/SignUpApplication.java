package uz.husan.signup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Random;

@EnableJpaAuditing
@SpringBootApplication
public class SignUpApplication {
    static {
        StringBuilder confCode = new StringBuilder();
        Random random = new Random();
        for (int j = 0; j < 6; j++) {
            int confCode1 = random.nextInt(9);
            confCode.append(confCode1);
        }
        System.out.println("######################################################################################");
        System.out.println("Generated Confirmation Code: " + confCode);
        System.out.println("######################################################################################");

    }
    public static void main(String[] args)
    {
        SpringApplication.run(SignUpApplication.class, args);
    }
}

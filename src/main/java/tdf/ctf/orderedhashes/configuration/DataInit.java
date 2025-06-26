package tdf.ctf.orderedhashes.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tdf.ctf.orderedhashes.services.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
public class DataInit {

    @Bean
    CommandLineRunner initDatabase(UserService userService) {
        return args -> {
            List<String> someUsernames = Arrays.asList(
                    "johndoe", "janedoe", "mikesmith",
                    "sarahlee", "davidkim", "emilywong",
                    "robertchen", "amyjones", "alexpark");
            for (String username : someUsernames) {
                userService.saveUser(username, generateRandomPassword(12, 16));
                System.out.println("Created user: " + username);
            }
            String targetPassword = generateEasyTargetPassword();
            System.out.println("Target password: " + targetPassword);

            userService.saveUser("target", targetPassword);
            System.out.println("Created target user");

            System.out.println("Database initialization completed");
        };
    }

    private String generateRandomPassword(int minLength, int maxLength) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        Random random = new Random();
        int length = random.nextInt(maxLength - minLength + 1) + minLength;

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    private String generateTargetPassword() {
        Random random = new Random();
        String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerChars = "abcdefghijklmnopqrstuvwxyz";
        String specialChars = "!@#$%^&*()";
        String digits = "0123456789";

        StringBuilder password = new StringBuilder();

        password.append(upperChars.charAt(random.nextInt(upperChars.length())));

        for (int i = 0; i < 5; i++) {
            password.append(lowerChars.charAt(random.nextInt(lowerChars.length())));
        }

        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));

        return password.toString();
    }

    private String generateEasyTargetPassword() {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

}

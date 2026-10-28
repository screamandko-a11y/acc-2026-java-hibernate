package bootcamp.hibernate_practical.config;

import bootcamp.hibernate_practical.entity.AppUser;
import bootcamp.hibernate_practical.entity.Role;
import bootcamp.hibernate_practical.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;



@Component
@Profile("demo")
@RequiredArgsConstructor
public class DemoUsersInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public void run(String[] args) {
        createIfMissing("user", "user", Role.USER);
        createIfMissing("admin", "admin", Role.ADMIN);

    }

    private void createIfMissing(
            String username,
            String rawPassword,
            Role role) {
        if (appUserRepository.findByUsername(username).isPresent()) {
            return;
        }

        AppUser user = new AppUser(
                username,
                passwordEncoder.encode(rawPassword),
                role
        );

        appUserRepository.save(user);

    }
}

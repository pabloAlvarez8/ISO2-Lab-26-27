package es.uclm.rental_platform.service;

import es.uclm.rental_platform.persistence.User;
import es.uclm.rental_platform.persistence.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User registerUser(String name, String email, String rawPassword, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        String assignedRole = (role == null || role.isBlank()) ? "TENANT" : role;

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashedPassword);
        newUser.setRole(assignedRole);

        return userRepository.save(newUser);
    }

    public User authenticate(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                return user;
            }
        }
        throw new IllegalArgumentException("Correo o contraseña incorrectos.");
    }
}
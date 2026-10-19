package es.uclm.rental_platform.service;

import es.uclm.rental_platform.persistence.User;
import es.uclm.rental_platform.persistence.UserRepository;
import es.uclm.rental_platform.service.exception.InvalidCredentialsException;
import es.uclm.rental_platform.service.exception.InvalidRegistrationDataException;
import es.uclm.rental_platform.service.exception.UserAlreadyExistsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
public class UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String name, String email, String rawPassword, String role) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo electrónico no puede estar vacío.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new InvalidRegistrationDataException("El formato del correo electrónico no es válido.");
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (rawPassword.length() < 8) {
            throw new InvalidRegistrationDataException("La contraseña debe tener al menos 8 caracteres.");
        }

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new UserAlreadyExistsException("El correo electrónico ya está registrado.");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        String assignedRole = (role == null || role.isBlank()) ? "TENANT" : role;

        User newUser = new User();
        newUser.setName(name.trim());
        newUser.setEmail(normalizedEmail);
        newUser.setPasswordHash(hashedPassword);
        newUser.setRole(assignedRole);

        return userRepository.save(newUser);
    }

    public User authenticate(String email, String rawPassword) {
        if (email == null || rawPassword == null) {
            throw new InvalidCredentialsException("Correo o contraseña incorrectos.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                return user;
            }
        }
        throw new InvalidCredentialsException("Correo o contraseña incorrectos.");
    }
}
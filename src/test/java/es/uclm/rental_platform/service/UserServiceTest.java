package es.uclm.rental_platform.service;

import es.uclm.rental_platform.persistence.User;
import es.uclm.rental_platform.persistence.UserRepository;
import es.uclm.rental_platform.service.exception.InvalidCredentialsException;
import es.uclm.rental_platform.service.exception.InvalidRegistrationDataException;
import es.uclm.rental_platform.service.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_WithValidData_SavesUserWithHashedPassword() {
        User registered = userService.registerUser("Carlos Ruiz", "carlos@example.com", "SecretPass123", "LANDLORD");

        assertNotNull(registered.getId());
        assertEquals("Carlos Ruiz", registered.getName());
        assertEquals("carlos@example.com", registered.getEmail());
        assertEquals("LANDLORD", registered.getRole());
        
        // Comprobar que no se guarda en texto plano y usa formato BCrypt
        assertNotEquals("SecretPass123", registered.getPasswordHash());
        assertTrue(registered.getPasswordHash().startsWith("$2a$") || registered.getPasswordHash().startsWith("$2b$"));
    }

    @Test
    void registerUser_WithPasswordExactly8Chars_Succeeds() {
        User registered = userService.registerUser("Marta Díaz", "marta@example.com", "12345678", "TENANT");

        assertNotNull(registered.getId());
        assertEquals("marta@example.com", registered.getEmail());
        assertNotEquals("12345678", registered.getPasswordHash());
    }

    @Test
    void registerUser_WithPasswordLessThan8Chars_ThrowsInvalidRegistrationDataException() {
        InvalidRegistrationDataException exception = assertThrows(
                InvalidRegistrationDataException.class,
                () -> userService.registerUser("Pedro Gómez", "pedro@example.com", "1234567", "TENANT")
        );

        assertEquals("La contraseña debe tener al menos 8 caracteres.", exception.getMessage());
        assertTrue(userRepository.findByEmail("pedro@example.com").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"correo-invalido", "sin-arroba.com", "@sinusuario.com", "usuario@sindominio", "usuario@dominio.c", "espacios en@correo.com"})
    void registerUser_WithInvalidEmailFormat_ThrowsInvalidRegistrationDataException(String invalidEmail) {
        InvalidRegistrationDataException exception = assertThrows(
                InvalidRegistrationDataException.class,
                () -> userService.registerUser("Usuario Test", invalidEmail, "PasswordSeguro1", "TENANT")
        );

        assertEquals("El formato del correo electrónico no es válido.", exception.getMessage());
        assertEquals(0, userRepository.count());
    }

    @Test
    void registerUser_WithExistingEmail_ThrowsUserAlreadyExistsException() {
        userService.registerUser("Ana Torres", "ana@example.com", "Password123", "TENANT");

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.registerUser("Ana Duplicada", "ana@example.com", "OtraClaveSegura", "TENANT")
        );

        assertEquals("El correo electrónico ya está registrado.", exception.getMessage());
    }

    @Test
    void registerUser_WithBlankFields_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser("", "correo@test.com", "PasswordSeguro1", "TENANT"));
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser("Nombre", "", "PasswordSeguro1", "TENANT"));
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser("Nombre", "correo@test.com", "", "TENANT"));
    }

    @Test
    void authenticate_WithValidCredentials_ReturnsAuthenticatedUser() {
        userService.registerUser("Laura Gómez", "laura@example.com", "MiClaveSegura1", "TENANT");

        User authenticated = userService.authenticate("laura@example.com", "MiClaveSegura1");

        assertNotNull(authenticated);
        assertEquals("Laura Gómez", authenticated.getName());
    }

    @Test
    void authenticate_WithInvalidPassword_ThrowsInvalidCredentialsException() {
        userService.registerUser("Laura Gómez", "laura@example.com", "MiClaveSegura1", "TENANT");

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.authenticate("laura@example.com", "ClaveErronea123")
        );

        assertEquals("Correo o contraseña incorrectos.", exception.getMessage());
    }

    @Test
    void authenticate_WithNonExistentEmail_ThrowsInvalidCredentialsException() {
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.authenticate("inexistente@example.com", "Cualquiera123")
        );

        assertEquals("Correo o contraseña incorrectos.", exception.getMessage());
    }

    @Test
    void authenticate_WithNullCredentials_ThrowsInvalidCredentialsException() {
        assertThrows(InvalidCredentialsException.class, () -> userService.authenticate(null, "Clave1234"));
        assertThrows(InvalidCredentialsException.class, () -> userService.authenticate("usuario@test.com", null));
    }
}

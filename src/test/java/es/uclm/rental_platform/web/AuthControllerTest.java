package es.uclm.rental_platform.web;

import es.uclm.rental_platform.persistence.User;
import es.uclm.rental_platform.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        userRepository.deleteAll();
    }

    @Test
    void login_WhenRequested_ReturnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("Iniciar sesión")))
                .andExpect(content().string(containsString("AlohaRent")));
    }

    @Test
    void login_WithRegisteredParam_ShowsSuccessAlert() throws Exception {
        mockMvc.perform(get("/login").param("registered", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("¡Registro completado!")));
    }

    @Test
    void register_WhenRequested_ReturnsRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(content().string(containsString("Crear cuenta")))
                .andExpect(content().string(containsString("AlohaRent")));
    }

    @Test
    void processRegister_WithValidData_PersistsUserAndAutoLoginsAndRedirectsToDashboard() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/register")
                        .param("name", "Elena Navarro")
                        .param("email", "elena@example.com")
                        .param("password", "MiPasswordSeguro1")
                        .param("role", "LANDLORD")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard?registered=true"));

        assertNotNull(session.getAttribute("loggedUser"));
        User sessionUser = (User) session.getAttribute("loggedUser");
        assertEquals("Elena Navarro", sessionUser.getName());

        User saved = userRepository.findByEmail("elena@example.com").orElse(null);
        assertNotNull(saved);
        assertNotNull(saved.getPasswordHash());
        assertEquals("LANDLORD", saved.getRole());
    }

    @Test
    void processRegister_WithPasswordShorterThan8Chars_ShowsRegisterViewWithErrorAndDoesNotPersist() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/register")
                        .param("name", "Usuario Corto")
                        .param("email", "corto@example.com")
                        .param("password", "1234567")
                        .param("role", "TENANT")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(content().string(containsString("La contraseña debe tener al menos 8 caracteres.")));

        assertTrue(userRepository.findByEmail("corto@example.com").isEmpty());
        assertTrue(session.getAttribute("loggedUser") == null);
    }

    @Test
    void processRegister_WithInvalidEmailFormat_ShowsRegisterViewWithErrorAndDoesNotPersist() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/register")
                        .param("name", "Usuario Formato Mal")
                        .param("email", "correo-invalido")
                        .param("password", "ClaveSegura123")
                        .param("role", "TENANT")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(content().string(containsString("El formato del correo electrónico no es válido.")));

        assertEquals(0, userRepository.count());
        assertTrue(session.getAttribute("loggedUser") == null);
    }

    @Test
    void processRegister_WithDuplicateEmail_ShowsRegisterViewWithError() throws Exception {
        mockMvc.perform(post("/register")
                .param("name", "Usuario Original")
                .param("email", "repetido@example.com")
                .param("password", "clave12345")
                .param("role", "TENANT")
                .with(csrf()));

        mockMvc.perform(post("/register")
                        .param("name", "Usuario Duplicado")
                        .param("email", "repetido@example.com")
                        .param("password", "otraclave123")
                        .param("role", "TENANT")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(content().string(containsString("El correo electrónico ya está registrado.")));
    }

    @Test
    void processLogin_WithCorrectCredentials_SetsSessionAndRedirectsHome() throws Exception {
        mockMvc.perform(post("/register")
                .param("name", "Marcos Soler")
                .param("email", "marcos@example.com")
                .param("password", "Marcos1234")
                .param("role", "TENANT")
                .with(csrf()));

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/login")
                        .param("email", "marcos@example.com")
                        .param("password", "Marcos1234")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertNotNull(session.getAttribute("loggedUser"));
    }

    @Test
    void processLogin_WithBadCredentials_ShowsLoginViewWithError() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "noexiste@example.com")
                        .param("password", "erronea123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("Correo o contraseña incorrectos.")));
    }

    @Test
    void dashboard_WhenAnonymous_ShowsOnlyLoginRegisterAndHomeOptions() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(content().string(containsString("Acceso a Panel de Usuario")))
                .andExpect(content().string(containsString("Iniciar sesión")))
                .andExpect(content().string(containsString("Registrarse")))
                .andExpect(content().string(containsString("Volver a inicio")))
                .andExpect(content().string(not(containsString("Cerrar sesión"))))
                .andExpect(content().string(not(containsString("Ver perfil"))));
    }

    @Test
    void dashboard_WhenAuthenticated_ShowsOnlyThreeOptions() throws Exception {
        User loggedUser = new User("Clara Vega", "clara@example.com", "$2a$10$dummyHash", "LANDLORD");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", loggedUser);

        mockMvc.perform(get("/dashboard").param("registered", "true").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(content().string(containsString("¡Bienvenido a AlohaRent!")))
                .andExpect(content().string(containsString("Ver perfil")))
                .andExpect(content().string(containsString("Volver a inicio")))
                .andExpect(content().string(containsString("Cerrar sesión")))
                .andExpect(content().string(not(containsString("Información de tu cuenta en la base de datos"))));
    }

    @Test
    void profile_WhenAnonymous_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    void profile_WhenAuthenticated_ReturnsProfileViewWithoutVerPerfilOption() throws Exception {
        User loggedUser = new User("Clara Vega", "clara@example.com", "$2a$10$dummyHash", "LANDLORD");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", loggedUser);

        mockMvc.perform(get("/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(content().string(containsString("Clara Vega")))
                .andExpect(content().string(containsString("clara@example.com")))
                .andExpect(content().string(containsString("Información de tu cuenta en la base de datos")))
                .andExpect(content().string(containsString("Volver a inicio")))
                .andExpect(content().string(containsString("Cerrar sesión")))
                .andExpect(content().string(not(containsString("Volver al panel"))))
                .andExpect(content().string(not(containsString("Ver perfil"))));
    }

    @Test
    void logout_WhenRequested_InvalidatesSessionAndRedirectsToLogin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", new User("User", "u@test.com", "h", "TENANT"));

        mockMvc.perform(get("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertTrue(session.isInvalid());
    }
}

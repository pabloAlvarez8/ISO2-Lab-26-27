package es.uclm.rental_platform.web;

import es.uclm.rental_platform.persistence.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class HomeControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void home_WhenAnonymous_ReturnsHomeViewWithBrandAndLoginLinks() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(containsString("AlohaRent")))
                .andExpect(content().string(containsString("Iniciar sesión")))
                .andExpect(content().string(containsString("Registrarse")))
                .andExpect(content().string(containsString("No hay alojamientos disponibles")));
    }

    @Test
    void home_WhenAuthenticatedInSession_ShowsUserGreetingAndLogout() throws Exception {
        User loggedUser = new User("Pablo Martín", "pablo@example.com", "$2a$10$dummyHash", "TENANT");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", loggedUser);

        mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(containsString("Pablo Martín")))
                .andExpect(content().string(containsString("Cerrar sesión")))
                .andExpect(content().string(containsString("No hay alojamientos disponibles")));
    }
}

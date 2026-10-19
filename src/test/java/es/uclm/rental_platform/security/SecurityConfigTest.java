package es.uclm.rental_platform.security;

import es.uclm.rental_platform.persistence.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityConfigTest {

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
    void publicEndpoints_WhenAnonymous_AreAccessible() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/login")).andExpect(status().isOk());
        mockMvc.perform(get("/register")).andExpect(status().isOk());
        mockMvc.perform(get("/dashboard")).andExpect(status().isOk());
    }

    @Test
    void protectedProfile_WhenAnonymous_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser
    void protectedProfile_WhenAuthenticated_IsAccessible() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", new User("Test User", "test@example.com", "hash", "TENANT"));

        mockMvc.perform(get("/profile").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void csrfProtection_WhenPostWithoutCsrfToken_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/register")
                        .param("name", "Insecure")
                        .param("email", "insecure@example.com")
                        .param("password", "Pass12345"))
                .andExpect(status().isForbidden());
    }

    @Test
    void csrfProtection_WhenPostWithCsrfToken_IsAccepted() throws Exception {
        mockMvc.perform(post("/register")
                        .param("name", "Secure")
                        .param("email", "secure@example.com")
                        .param("password", "Pass12345")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void logout_WhenAnonymousOrAuthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}

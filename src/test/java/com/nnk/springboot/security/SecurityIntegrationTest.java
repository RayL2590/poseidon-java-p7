package com.nnk.springboot.security;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

/**
 * Tests d'intégration de sécurité pour l'application Poseidon Trading.
 * 
 * <p>Cette classe teste l'intégration complète du système de sécurité Spring Security,
 * incluant l'authentification, l'autorisation et la navigation sécurisée.</p>
 * 
 * <p>Domaines testés :</p>
 * <ul>
 *   <li><strong>Authentification</strong> : Login/logout avec credentials valides/invalides</li>
 *   <li><strong>Autorisation</strong> : Accès aux pages selon les rôles</li>
 *   <li><strong>Redirection</strong> : Pages login, succès, erreurs</li>
 *   <li><strong>Session management</strong> : Gestion des sessions utilisateur</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Nettoyage et création des utilisateurs de test
        userRepository.deleteAll();

        // Utilisateur standard
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("TestPass123!"));
        testUser.setFullname("Test User");
        testUser.setRole("USER");
        userRepository.save(testUser);

        // Utilisateur administrateur
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("AdminPass123!"));
        adminUser.setFullname("Admin User");
        adminUser.setRole("ADMIN");
        userRepository.save(adminUser);
    }

    // ================================
    // Tests d'accès sans authentification
    // ================================

    @Test
    void accessHome_ShouldRedirectToLogin_WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void accessLogin_ShouldBeAllowed_WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void accessSecurePage_ShouldRedirectToLogin_WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void accessUserManagement_ShouldRedirectToLogin_WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/user/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // ================================
    // Tests d'authentification
    // ================================

    @Test
    void loginWithValidCredentials_ShouldSucceed() throws Exception {
        mockMvc.perform(formLogin().user("testuser").password("TestPass123!"))
                .andExpect(authenticated().withUsername("testuser"))
                .andExpect(redirectedUrl("/bidList/list"));
    }

    @Test
    void loginWithValidAdminCredentials_ShouldSucceed() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("AdminPass123!"))
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"))
                .andExpect(redirectedUrl("/bidList/list"));
    }

    @Test
    void loginWithInvalidUsername_ShouldFail() throws Exception {
        mockMvc.perform(formLogin().user("wronguser").password("TestPass123!"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void loginWithInvalidPassword_ShouldFail() throws Exception {
        mockMvc.perform(formLogin().user("testuser").password("WrongPass123!"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void loginWithEmptyCredentials_ShouldFail() throws Exception {
        mockMvc.perform(formLogin().user("").password(""))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    // ================================
    // Tests de déconnexion
    // ================================

    @Test
    void logout_ShouldSucceed() throws Exception {
        // Login d'abord
        mockMvc.perform(formLogin().user("testuser").password("TestPass123!"))
                .andExpect(authenticated());

        // Puis logout
        mockMvc.perform(logout())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?logout=true"));
    }

    // ================================
    // Tests d'autorisation par rôles
    // ================================

    @Test
    void accessSecureArea_ShouldSucceed_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/secure/article-details")
                .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"));
    }

    @Test
    void accessUserList_ShouldSucceed_WhenAuthenticatedAsUser() throws Exception {
        mockMvc.perform(get("/user/list")
                .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"));
    }

    @Test
    void accessUserList_ShouldSucceed_WhenAuthenticatedAsAdmin() throws Exception {
        mockMvc.perform(get("/user/list")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"));
    }

    @Test
    void accessBidListAdd_ShouldSucceed_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/bidList/add")
                .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"));
    }

    // ================================
    // Tests de gestion d'erreurs d'autorisation
    // ================================

    @Test
    void accessErrorPage_ShouldShowAccessDenied_WhenConfigured() throws Exception {
        mockMvc.perform(get("/app/error")
                .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("403"))
                .andExpect(model().attributeExists("errorMsg"));
    }

    // ================================
    // Tests de workflow complet
    // ================================

    @Test
    void completeLoginWorkflow_ShouldWork() throws Exception {
        // 1. Accéder à une page sécurisée sans authentification -> redirection login
        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        // 2. Aller à la page de login
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

        // 3. Se connecter avec des credentials valides
        mockMvc.perform(formLogin().user("testuser").password("TestPass123!"))
                .andExpect(authenticated().withUsername("testuser"))
                .andExpect(redirectedUrl("/bidList/list"));

        // 4. Accéder à la page sécurisée après authentification
        mockMvc.perform(get("/bidList/list")
                .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"));

        // 5. Se déconnecter
        mockMvc.perform(logout())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?logout=true"));

        // 6. Vérifier que l'accès aux pages sécurisées est à nouveau interdit
        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // ================================
    // Tests de validation des mots de passe
    // ================================

    @Test
    void loginWithWeakPassword_ShouldStillWork_IfStoredInDatabase() throws Exception {
        // Ce test vérifie que même si le mot de passe est faible,
        // si il est stocké en base, l'authentification fonctionne
        // (la validation du mot de passe se fait à la création/modification)
        
        User weakPasswordUser = new User();
        weakPasswordUser.setUsername("weakuser");
        weakPasswordUser.setPassword(passwordEncoder.encode("weak")); // Mot de passe faible mais encodé
        weakPasswordUser.setFullname("Weak Password User");
        weakPasswordUser.setRole("USER");
        userRepository.save(weakPasswordUser);

        mockMvc.perform(formLogin().user("weakuser").password("weak"))
                .andExpect(authenticated().withUsername("weakuser"));
    }
}
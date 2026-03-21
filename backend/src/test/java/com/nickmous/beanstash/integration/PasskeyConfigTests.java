package com.nickmous.beanstash.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nickmous.beanstash.configuration.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@Import(TestcontainersConfig.class)
@ContextConfiguration(initializers = TestcontainersConfig.Initializer.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PasskeyConfigTests {

    @Autowired
    private PublicKeyCredentialUserEntityRepository publicKeyCredentialUserEntityRepository;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void webAuthnRepositoryBeansAreLoaded() {
        assertNotNull(publicKeyCredentialUserEntityRepository);
        assertNotNull(userCredentialRepository);
    }

    @Test
    void passwordEncoderBeanIsLoaded() {
        assertNotNull(passwordEncoder);
    }

    @Test
    void webAuthnRegistrationEndpointDeniesUnauthenticatedAccess() throws Exception {
        mockMvc.perform(post("/webauthn/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    void webAuthnRegistrationOptionsEndpointRejectsBadRequest() throws Exception {
        mockMvc.perform(post("/webauthn/register/options")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void webAuthnAuthenticationOptionsEndpointIsAccessible() throws Exception {
        mockMvc.perform(post("/webauthn/authenticate/options")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}

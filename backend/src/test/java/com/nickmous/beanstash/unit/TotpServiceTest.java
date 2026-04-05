package com.nickmous.beanstash.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.nickmous.beanstash.domain.security.totp.Totp;
import com.nickmous.beanstash.domain.security.totp.TotpService;
import com.nickmous.beanstash.domain.security.totp.TotpSetupResponse;
import com.nickmous.beanstash.entity.User;
import com.nickmous.beanstash.repository.UserRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TotpServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TotpService totpService;

    @Test
    void setupTotp_generatesSecretAndUri() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        TotpSetupResponse response = totpService.setupTotp(user);

        assertThat(response.secret()).isNotEmpty();
        assertThat(response.otpAuthUri()).startsWith("otpauth://totp/");
        assertThat(response.otpAuthUri()).contains("test%40example.com");
    }

    @Test
    void setupTotp_setsSecretOnUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        totpService.setupTotp(user);

        assertThat(user.getTotpSecret()).isNotNull();
        assertThat(user.getTotpSecret()).hasSize(20);
    }

    @Test
    void setupTotp_doesNotEnableTotp() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        totpService.setupTotp(user);

        assertThat(user.isTotpEnabled()).isFalse();
    }

    @Test
    void setupTotp_savesUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        totpService.setupTotp(user);

        verify(userRepository).save(user);
    }

    @Test
    void verifyAndEnableTotp_enablesOnValidCode() {
        User user = new User();
        byte[] secret = Totp.generateSecret();
        user.setTotpSecret(secret);
        user.setTotpEnabled(false);

        String validCode = Totp.generateCode(secret, Instant.now());

        boolean result = totpService.verifyAndEnableTotp(user, validCode);

        assertThat(result).isTrue();
        assertThat(user.isTotpEnabled()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void verifyAndEnableTotp_rejectsInvalidCode() {
        User user = new User();
        user.setTotpSecret(Totp.generateSecret());
        user.setTotpEnabled(false);

        boolean result = totpService.verifyAndEnableTotp(user, "000000");

        assertThat(result).isFalse();
        assertThat(user.isTotpEnabled()).isFalse();
    }

    @Test
    void verifyCode_returnsTrueForValidCode() {
        User user = new User();
        byte[] secret = Totp.generateSecret();
        user.setTotpSecret(secret);
        user.setTotpEnabled(true);

        String validCode = Totp.generateCode(secret, Instant.now());

        assertThat(totpService.verifyCode(user, validCode)).isTrue();
    }

    @Test
    void verifyCode_returnsFalseForInvalidCode() {
        User user = new User();
        user.setTotpSecret(Totp.generateSecret());
        user.setTotpEnabled(true);

        assertThat(totpService.verifyCode(user, "000000")).isFalse();
    }

    @Test
    void verifyCode_returnsFalseWhenTotpNotEnabled() {
        User user = new User();
        user.setTotpSecret(Totp.generateSecret());
        user.setTotpEnabled(false);

        String validCode = Totp.generateCode(user.getTotpSecret(), Instant.now());

        assertThat(totpService.verifyCode(user, validCode)).isFalse();
    }
}

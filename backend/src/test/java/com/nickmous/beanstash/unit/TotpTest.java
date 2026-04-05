package com.nickmous.beanstash.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.nickmous.beanstash.domain.security.totp.Totp;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TotpTest {

    // RFC 6238 test seed: "12345678901234567890" as ASCII bytes
    private static final byte[] RFC_TEST_SECRET = "12345678901234567890".getBytes(StandardCharsets.US_ASCII);

    @Test
    void generateCode_rfc6238TestVector_time59() {
        Instant time = Instant.ofEpochSecond(59);
        String code = Totp.generateCode(RFC_TEST_SECRET, time);
        assertThat(code).isEqualTo("287082");
    }

    @Test
    void generateCode_rfc6238TestVector_time1111111109() {
        Instant time = Instant.ofEpochSecond(1111111109);
        String code = Totp.generateCode(RFC_TEST_SECRET, time);
        assertThat(code).isEqualTo("081804");
    }

    @Test
    void generateCode_rfc6238TestVector_time1234567890() {
        Instant time = Instant.ofEpochSecond(1234567890);
        String code = Totp.generateCode(RFC_TEST_SECRET, time);
        assertThat(code).isEqualTo("005924");
    }

    @Test
    void generateCode_returnsSixDigitString() {
        String code = Totp.generateCode(RFC_TEST_SECRET, Instant.now());
        assertThat(code).hasSize(6);
        assertThat(code).matches("\\d{6}");
    }

    @Test
    void verifyCode_acceptsCurrentTimeStep() {
        Instant now = Instant.ofEpochSecond(59);
        String code = Totp.generateCode(RFC_TEST_SECRET, now);
        assertThat(Totp.verifyCode(RFC_TEST_SECRET, code, now, 1)).isTrue();
    }

    @Test
    void verifyCode_acceptsPreviousTimeStep() {
        Instant now = Instant.ofEpochSecond(59);
        String codeFromPreviousStep = Totp.generateCode(RFC_TEST_SECRET, Instant.ofEpochSecond(0));
        assertThat(Totp.verifyCode(RFC_TEST_SECRET, codeFromPreviousStep, now, 1)).isTrue();
    }

    @Test
    void verifyCode_rejectsTwoStepsAgo() {
        Instant now = Instant.ofEpochSecond(90);
        String codeFromTwoStepsAgo = Totp.generateCode(RFC_TEST_SECRET, Instant.ofEpochSecond(0));
        assertThat(Totp.verifyCode(RFC_TEST_SECRET, codeFromTwoStepsAgo, now, 1)).isFalse();
    }

    @Test
    void verifyCode_rejectsInvalidCode() {
        assertThat(Totp.verifyCode(RFC_TEST_SECRET, "000000", Instant.ofEpochSecond(59), 1)).isFalse();
    }

    @Test
    void verifyCode_rejectsNullCode() {
        assertThat(Totp.verifyCode(RFC_TEST_SECRET, null, Instant.now(), 1)).isFalse();
    }

    @Test
    void verifyCode_rejectsEmptyCode() {
        assertThat(Totp.verifyCode(RFC_TEST_SECRET, "", Instant.now(), 1)).isFalse();
    }

    @Test
    void verifyCode_returnsFalseWhenSecretIsNull() {
        assertThat(Totp.verifyCode(null, "123456", Instant.now(), 1)).isFalse();
    }

    @Test
    void verifyCode_returnsFalseWhenSecretIsEmpty() {
        assertThat(Totp.verifyCode(new byte[0], "123456", Instant.now(), 1)).isFalse();
    }

    @Test
    void generateSecret_returns20Bytes() {
        byte[] secret = Totp.generateSecret();
        assertThat(secret).hasSize(20);
    }

    @Test
    void generateSecret_producesDifferentSecrets() {
        byte[] secret1 = Totp.generateSecret();
        byte[] secret2 = Totp.generateSecret();
        assertThat(secret1).isNotEqualTo(secret2);
    }

    @Test
    void secretToBase32_returnsValidBase32String() {
        byte[] secret = Totp.generateSecret();
        String base32 = Totp.secretToBase32(secret);
        assertThat(base32).matches("[A-Z2-7]+=*");
        assertThat(base32).isNotEmpty();
    }

    @Test
    void buildOtpAuthUri_containsRequiredComponents() {
        byte[] secret = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        String uri = Totp.buildOtpAuthUri("Beanstash", "user@example.com", secret);
        assertThat(uri).startsWith("otpauth://totp/");
        assertThat(uri).contains("Beanstash");
        assertThat(uri).contains("user%40example.com");
        assertThat(uri).contains("secret=");
        assertThat(uri).contains("issuer=Beanstash");
        assertThat(uri).contains("algorithm=SHA1");
        assertThat(uri).contains("digits=6");
        assertThat(uri).contains("period=30");
    }
}

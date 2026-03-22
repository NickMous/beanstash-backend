package com.nickmous.beanstash.domain.security.totp;

import com.nickmous.beanstash.entity.User;
import com.nickmous.beanstash.repository.UserRepository;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TotpService {

    private static final String ISSUER = "Beanstash";
    private static final int WINDOW_SIZE = 1;

    private final UserRepository userRepository;

    public TotpSetupResponse setupTotp(User user) {
        byte[] secret = Totp.generateSecret();
        user.setTotpSecret(secret);
        userRepository.save(user);

        String base32Secret = Totp.secretToBase32(secret);
        String otpAuthUri = Totp.buildOtpAuthUri(ISSUER, user.getEmail(), secret);

        return new TotpSetupResponse(base32Secret, otpAuthUri);
    }

    public boolean verifyAndEnableTotp(User user, String code) {
        if (!Totp.verifyCode(user.getTotpSecret(), code, Instant.now(), WINDOW_SIZE)) {
            return false;
        }
        user.setTotpEnabled(true);
        userRepository.save(user);
        return true;
    }

    public boolean verifyCode(User user, String code) {
        if (!user.isTotpEnabled()) {
            return false;
        }
        return Totp.verifyCode(user.getTotpSecret(), code, Instant.now(), WINDOW_SIZE);
    }
}

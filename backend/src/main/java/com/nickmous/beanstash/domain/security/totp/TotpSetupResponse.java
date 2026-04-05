package com.nickmous.beanstash.domain.security.totp;

public record TotpSetupResponse(String secret, String otpAuthUri) {
}

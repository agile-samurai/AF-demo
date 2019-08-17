package com.u.group.hsmgateway.config;

import com.cavium.cfm2.CFM2Exception;
import org.springframework.context.annotation.Bean;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    void loginToHSM() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Key aesKey = null;

        try {
            aesKey = SymmetricKeys.generateAESKey(256, "Implicit Java Properties Login Key");
        } catch (Exception e) {
            if (CFM2Exception.isAuthenticationFailure(e)) {
                System.out.printf("\nDetected invalid credentials\n\n");
                e.printStackTrace();
                return;
            }

            throw e;
        }

        System.out.printf("\nLogin successful!\n\n");
    }
}

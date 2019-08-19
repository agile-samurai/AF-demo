package com.u.group.hsmgateway.config;

import com.cavium.cfm2.CFM2Exception;
import com.cavium.key.CaviumKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;

import java.security.*;

@org.springframework.context.annotation.Configuration
public class Configuration {

    private Logger logger = LogManager.getLogger(this);
    public static final String MASTER_KEY_LABEL = "Master KeyStoreLabel";

    @Bean
    KeyStore getKeyStore() throws Exception {
        KeyStore keyStore;
        try {
            Security.addProvider(new com.cavium.provider.CaviumProvider());
            keyStore = KeyStore.getInstance("Cavium");
            keyStore.load(null, null);

            System.out.printf("The KeyStore contains %d keys\n", keyStore.size());

            if (keyStore.containsAlias(MASTER_KEY_LABEL)) {
                // If using implicit credentials, the getKeyByHandle() method will kickoff the first authentication attempt.
                // If the session is already authenticated, then getKeyByHandle() will simply reach out to the HSM.
                Key k = keyStore.getKey(MASTER_KEY_LABEL, null);
            } else {
                logger.info("Generating Master Key");
                final Key k = SymmetricKeys.generateAESKey(256, MASTER_KEY_LABEL);
                logger.info("Generated master key label:" + ((CaviumKey) k).getLabel() + "\n");
                System.out.print("Generated master key handle: " + ((CaviumKey) k).getHandle() + "\n");
            }

        } catch (Exception e) {
            if (CFM2Exception.isAuthenticationFailure(e)) {
                logger.error("\nDetected invalid credentials\n\n");
                e.printStackTrace();
            }

            throw e;
        }

        logger.info("\nLogin successful!\n\n");
        return keyStore;
    }
}

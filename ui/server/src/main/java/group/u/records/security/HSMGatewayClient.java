package group.u.records.security;

import org.springframework.stereotype.Component;

@Component
public class HSMGatewayClient {
    public String encrypt(String content) {
        return content;
    }
}

package group.u.records.security;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class AWSCloudHSMSecurityGatewayClientTest {

    @Test
    @Ignore
    public void shouldEncryptData(){

        AWSCloudHSMSecurityGatewayClient client = new AWSCloudHSMSecurityGatewayClient(new RestTemplate(), "http://13.52.124.47:8080" );
        String content = "this is a random text string";
        UUID id = UUID.randomUUID();
        String encrypt = client.encrypt(id, content);

        System.out.println(encrypt);
        assertThat(encrypt).isNotNull();

        assertThat(content).isEqualTo(client.decrypt(id, encrypt));
    }

    @Test
    @Ignore
    public void shouldFailBasedOnCurrentBug(){
        AWSCloudHSMSecurityGatewayClient client = new AWSCloudHSMSecurityGatewayClient(new RestTemplate(), "http://13.52.124.47:8080" );
        String content = "this is a random text string";
        String secondContent = "This is secondContent";
        UUID id = UUID.randomUUID();
        String encrypt = client.encrypt(id, content);
        String secondEncrypt = client.encrypt(id, secondContent);

        System.out.println(encrypt);
        assertThat(encrypt).isNotNull();
        assertThat(secondContent).isEqualTo(client.decrypt(id, secondEncrypt));
        assertThat(content).isEqualTo(client.decrypt(id, encrypt));

    }
}

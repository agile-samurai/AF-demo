package group.u.mdas.service;

import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class WebClientFactoryTest {

    @Test
    public void shouldSetAllWebClientOptions(){
        WebClient client = new WebClientFactory().getWebClient();

        assertThat(client.getOptions().isJavaScriptEnabled()).isFalse();
        assertThat(client.getOptions().isCssEnabled()).isFalse();
        assertThat(client.getOptions().getTimeout()).isEqualTo(60000);
    }

}

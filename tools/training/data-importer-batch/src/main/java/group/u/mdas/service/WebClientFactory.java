package group.u.mdas.service;

import com.gargoylesoftware.htmlunit.WebClient;
import org.springframework.stereotype.Component;

@Component
public class WebClientFactory {

    public WebClient getWebClient() {
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setDownloadImages(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setTimeout(60000);
        return webClient;
    }

}

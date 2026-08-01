package tancredidangelo.capstone.emailSender;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tancredidangelo.capstone.entities.person.user.stack.User;


@Slf4j
@Component
public class EmailSender {

    private final String domainName;
    private final String apiKey;

    public EmailSender(
            @Value("${mailgun.domainName}") String domainName,
            @Value("${mailgun.apiKey}") String apiKey
    ) {
        this.domainName = domainName;
        this.apiKey = apiKey;
    }

    @Async
    public void sendRegistrationEmail(User recipient) {
        try {
            HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                    .basicAuth("api", this.apiKey)
                    .queryString("from", "tancredi.dangelo.22@gmail.com")
                    .queryString("to", recipient.getEmail())
                    .queryString("subject", "Welcome onboard!")
                    .queryString("text", "Hello " + recipient.getFirstName() + ", your registration is completed!")
                    .asJson();

            if (response.getStatus() > 400) {
                log.warn("Attempt to send registration email returned: Response Status {} - {}", response.getStatus(), response.getStatusText());
            } else {
                log.info("Email successfully sent.");
            }

        } catch (Exception ex) {
           ex.printStackTrace();
           log.warn("Error occurred. Failed to send email.");

        }



    }

}

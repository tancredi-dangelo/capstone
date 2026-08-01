package tancredidangelo.capstone.emailSender;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import tancredidangelo.capstone.entities.person.user.User;

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

    public void sendRegistrationEmail(User recipient) {
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", "tancredi.dangelo.22@gmail.com")
                .queryString("to", recipient.getEmail())
                .queryString("subject", "Welcome onboard!")
                .queryString("text", "Hello " + recipient.getFirstName() + ", your registration is completed!")
                .asJson();

        System.out.println(response.getStatus());
    }

}

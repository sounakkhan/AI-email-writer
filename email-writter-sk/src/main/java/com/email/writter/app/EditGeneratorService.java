package com.email.writter.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EditGeneratorService {
    private final WebClient webClient;
@Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String  geminiApiKey;

    public EditGeneratorService(WebClient.Builder webClient) {
        this.webClient = WebClient.builder().build();
    }

    public String generateEmailReply(Emailrequest emailrequest){
        // build the prompt
        String prompt= buildPrompt(emailrequest);
        //create request
        Map<String,Object> requestBody= Map.of(
                "contents",new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })

                }
        );
        // do req and get response
        String response=webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("content-type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
//        extract respose and return
        return extractResponseContent(response);


    }

    private String extractResponseContent(String response) {
       try{
           ObjectMapper mapper= new ObjectMapper();
           JsonNode rootNode= mapper.readTree(response);
           return rootNode.path("candidates")
                   .get(0)
                   .path("content")
                   .path("parts")
                   .get(0)
                   .path("text")
                   .asText();


       }
       catch ( Exception e){
            return "Error processing message:"+e.getMessage();
       }
    }

    private String buildPrompt(Emailrequest emailrequest) {
        StringBuilder prompt= new StringBuilder();
        prompt.append("generate professional email reply for the following email content.pls don't generate a subject line");
        if(emailrequest.getTone()!=null && emailrequest.getTone().isEmpty()){
            prompt.append("use a").append(emailrequest.getTone()).append("tone.");
        }
        prompt.append(" \noriginal email:\n").append(emailrequest.getEmailContent());
        return prompt.toString();
    }
}

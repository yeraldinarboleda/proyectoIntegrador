// filepath: c:\Users\sebas\OneDrive\Escritorio\U\noveno semestre\proyecto integrador\proyecto\demo\src\main\java\proymodpredictivoia\demo\ai\AIService.java
package proymodpredictivoia.demo.ai;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIService {

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    public String generateContent(String text) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String apiKey = System.getProperty("API_GEMINI_KEY");
        String requestBody = "{ \"contents\": [{ \"parts\":[{\"text\": \"" + text + "\"}] }] }";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(API_URL + apiKey, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
}
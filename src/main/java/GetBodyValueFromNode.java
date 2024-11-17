import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GetBodyValueFromNode {
    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();

        String urlString = "http://localhost:8080/insights/executeJar";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            System.out.println("Código da Resposta: " + statusCode);

            if (statusCode == 200) {
                String responseBody = response.body();
                System.out.println("Resposta do Node.js: " + responseBody);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);

                String data = rootNode.path("data").asText();


            } else {
                System.out.println("Erro ao chamar o servidor. Código de status: " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
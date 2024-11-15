import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GetDepIdFromNode {
    public static void main(String[] args) {
        // Criação de um cliente HTTP
        HttpClient client = HttpClient.newHttpClient();

        // URL da sua rota Node.js
        String urlString = "http://localhost:8080/insights/executeJar";

        // Corpo da requisição em formato JSON
        String jsonInputString = "{\"DEPARTAMENTO_USUARIO\": \"1234\"}";

        // Criação da requisição HTTP (POST) com o corpo JSON
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString, StandardCharsets.UTF_8))
                .build();

        try {
            // Enviar a requisição e obter a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            System.out.println("Código da Resposta: " + statusCode);

            // Tratar a resposta conforme o código de status
            if (statusCode == 200) {
                String responseBody = response.body();
                System.out.println("Resposta do Node.js: " + responseBody);
            } else {
                System.out.println("Erro ao chamar o servidor. Código de status: " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

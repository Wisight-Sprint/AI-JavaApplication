import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeminiOperations {
    private static final String API_KEY = "<<key>>";
    private static String context;
    private static String fullPrompt;
    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:streamGenerateContent";

    public void generateInsight (String dataDep, String context) throws IOException, InterruptedException{

        fullPrompt = context + "\n" + dataDep;

        String jsonRequest = "{\"contents\":[{\"parts\":[{\"text\":\"" + fullPrompt + "\"}],\"role\":\"user\"}]}";

        HttpClient httpClient = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "?alt=sse&key=" + API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        processResponse(response);
    }

    public String getUserContextType(String insightKey) {
        if (insightKey.chars().anyMatch(c -> Character.isDigit(c))) {
            return "contexto usuário comum";
        } else {
            return "contexto usuário especial";
        }
    }

    private static void processResponse(HttpResponse<String> response) throws IOException {
        if (response.statusCode() != 200) {
            System.out.println("Erro: " + response.statusCode());
            return;
        }
        var pattern = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]+)\"");
        var answer = new StringBuilder();
        String line;
        try (var reader = new BufferedReader(new StringReader(response.body()))) {
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                Matcher matcher = pattern.matcher(line.substring(5));
                if (matcher.find()) {
                    answer.append(matcher.group(1)).append(" ");
                }
            }
        }
        System.out.println("answer: " + answer.toString());
    }
}

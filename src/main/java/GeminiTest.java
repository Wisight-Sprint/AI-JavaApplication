import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GeminiTest {

    private final JdbcTemplate connection;

    private static final String API_KEY = "<<Adicione sua chave aqui>>";
    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:streamGenerateContent";
    private static final ArrayList<String> history = new ArrayList<>();
    private static final int CAPACITY = 100;
    private static final String CONTEXT = "Esse é o contexto fixo para a IA. Utilize os dados fornecidos para responder de forma clara e objetiva.";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/seu_banco"; // Ajuste conforme necessário
    private static final String DB_USER = "seu_usuario";
    private static final String DB_PASSWORD = "sua_senha";

    public GeminiTest(JdbcTemplate connection) {
        this.connection = connection;
    }

    public static void main(String[] args) {
        try {
            // Gera os dados a partir do banco de dados
            List<String> data = generateDataFromDatabase();
            if (data.isEmpty()) {
                System.out.println("Erro: Nenhum dado encontrado no banco.");
                return;
            }

            // Define o prompt para a IA
            String prompt = "Qual é a análise baseada nos dados fornecidos?";
            sendRequest(prompt, data);

        } catch (Exception e) {
            System.err.println("Erro durante a execução: " + e.getMessage());
        }
    }

    private static void sendRequest(String prompt, List<String> data) throws IOException, InterruptedException {
        // Combina o contexto fixo, o histórico e os dados do banco
        var fullPrompt = CONTEXT + "\n" +
                "Histórico de conversa:\n" + history.stream().collect(Collectors.joining("\n")) + "\n" +
                "Dados do banco de dados:\n" + String.join(", ", data) + "\n" +
                "Pergunta: " + prompt;

        conversationHistory(prompt);

        var jsonRequest = "{\"contents\":[{\"parts\":[{\"text\":\"" + fullPrompt + "\"}],\"role\":\"user\"}]}";
        var httpClient = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "?alt=sse&key=" + API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        processResponse(response);
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

    private static String conversationHistory(String prompt) {
        if (history.size() == CAPACITY) {
            history.remove(0);
        }
        history.add(prompt);
        return history.stream().collect(Collectors.joining("\n"));
    }

    /**
     * Gera uma lista de dados a partir de um SELECT no banco de dados.
     *
     * @return Lista de Strings representando os dados do banco.
     */
    private static List<String> generateDataFromDatabase() {
        List<String> data = new ArrayList<>();
        String query = "SELECT coluna FROM tabela"; // Ajuste conforme necessário

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                data.add(resultSet.getString("coluna"));
            }

        } catch (Exception e) {
            System.err.println("Erro ao acessar o banco de dados: " + e.getMessage());
        }
        return data;
    }
}

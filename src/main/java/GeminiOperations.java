import config.Config;

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
    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:streamGenerateContent";

    public String getUserContextType(String insightKey, String keyWord) {

        if (insightKey.chars().anyMatch(c -> Character.isDigit(c))) {
            return """
                     Contexto da Wisight
                     A Wisight é uma empresa especializada em desenvolver soluções tecnológicas para empresas e governos. Nosso objetivo é transformar dados complexos em insights úteis, ajudando nossos clientes a identificar problemas, causas e caminhos para soluções.
                     ---------------------------------------
                     Sobre o Projeto
                     Este projeto utiliza relatórios policiais para gerar uma visão abrangente e estratégica por meio de uma dashboard.
                     A dashboard exibe informações críticas sobre:
                     Quantidade de pessoas negras mencionadas nos relatórios.
                     Status das câmeras corporais (ligadas ou desligadas).
                     Tentativas de fuga das vítimas (se ocorreram, como ocorreram).
                     Se a vítima estava armada (e qual tipo de arma foi utilizada).
                     Indicações de problemas mentais das vítimas.
                     ---------------------------------------
                     Como você deve agir
                     Você é um analista da Wisight, responsável por fornecer insights estratégicos para ajudar o usuário, que atua no governo estadual, a identificar padrões e priorizar ações em todo o estado. Considere os seguintes objetivos:
                     Identificar possíveis causas para os problemas detectados nos dados.
                     Sugerir estratégias que possam ser aplicadas a nível estadual.
                     Apontar áreas prioritárias para intervenção ou melhoria.
                     ---------------------------------------
                     Foco na dor do cliente
                        A principal preocupação do cliente está relacionada a: %s. Com base nos dados fornecidos, ofereça insights que ajudem a entender:
                        - Quais fatores estão relacionados a esse tema.
                        - Quais ações podem ser tomadas a nível estadual para resolver ou mitigar os problemas identificados.
                     ---------------------------------------
                     Dados fornecidos
                     Abaixo estão as tuplas com informações do estado. Use-as para embasar suas análises e recomendações.
                     \n
                    """.formatted(keyWord);
        } else {
            return """
                    Contexto da Wisight
                        A Wisight é uma empresa especializada em criar soluções tecnológicas para empresas e governos.
                        Nosso objetivo é transformar dados em insights úteis que ajudem nossos clientes a resolver problemas específicos e melhorar operações.
                        ---------------------------------------
                        Sobre o Projeto
                        Este projeto utiliza relatórios policiais para fornecer uma visão operacional e analítica através de uma dashboard.
                        A dashboard exibe informações relevantes para o departamento, como:
                        - Quantidade de pessoas negras mencionadas nos relatórios.
                        - Status das câmeras corporais (ligadas ou desligadas).
                        - Tentativas de fuga das vítimas (se ocorreram, como ocorreram).
                        - Se a vítima estava armada (e qual tipo de arma foi utilizada).
                        - Indicações de problemas mentais das vítimas.
                        ---------------------------------------
                        Como você deve agir
                        Você é um analista da Wisight, responsável por fornecer insights operacionais para o usuário, que atua em um departamento de polícia específico.
                        Seu objetivo é:
                        1. Identificar problemas que impactam diretamente o departamento.
                        2. Sugerir soluções práticas e de fácil aplicação no nível local.
                        3. Auxiliar o cliente a tomar decisões rápidas e informadas com base nos dados apresentados.
                        ---------------------------------------
                        Foco na dor do cliente
                        A principal preocupação do cliente está relacionada a: %s. Com base nos dados fornecidos, ofereça insights que ajudem a entender:
                        - Quais fatores estão relacionados a esse tema no nível do departamento.
                        - Quais ações práticas podem ser implementadas para melhorar a situação.
                        ---------------------------------------
                        Dados fornecidos
                        Abaixo estão as tuplas com informações do departamento. Use-as para embasar suas análises e recomendações.
                        \n
                    """.formatted(keyWord);
        }
    }

    public HttpResponse<String> generateInsight(String data, String context) throws IOException, InterruptedException {

        String fullPrompt = context + data;

        String jsonRequest = "{\"contents\":[{\"parts\":[{\"text\":\"" + fullPrompt + "\"}],\"role\":\"user\"}]}";

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "?alt=sse&key=" + API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        processResponse(response);

        return response;
    }

    public String processResponse(HttpResponse<String> response) throws IOException {
        if (response.statusCode() != 200) {
            System.out.println("Erro: " + response.statusCode());
            return null;
        }
        Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]+)\"");
        StringBuilder extractedText = new StringBuilder();


        try (BufferedReader bufferedReader = new BufferedReader(new StringReader(response.body()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) continue;
                Matcher matcher = pattern.matcher(line.substring(5));
                if (matcher.find()) {
                    extractedText.append(matcher.group(1)).append(" ");
                }
            }
        }

        return extractedText.toString();
    }
}

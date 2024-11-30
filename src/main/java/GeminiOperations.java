import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class GeminiOperations {

    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBVeSciUbmZP4qPYqqHMk3Wrq4sSACKwXo";

    public String getUserContextType(String insightKey1, String keyWord) {

        if (insightKey1.chars().anyMatch(c -> Character.isDigit(c))) {
            return """
                    Contexto da Wisight
                    A Wisight é uma empresa especializada em criar soluções tecnológicas para empresas e governos.
                    Nosso objetivo é transformar dados em insights úteis que ajudem nossos clientes a resolver problemas específicos e melhorar operações.
                    ---------------------------------------
                    Sobre o Projeto
                    Este projeto utiliza relatórios policiais para fornecer uma visão operacional e analítica através de uma dashboard.
                    A dashboard exibe informações relevantes para o departamento, como:
                    - Etnia das vítimas nos relatórios.
                    - Status das câmeras corporais (ligadas ou desligadas).
                    - Tentativas de fuga das vítimas (se ocorreram, como ocorreram).
                    - Se a vítima estava armada (e qual tipo de arma foi utilizada).
                    - Indicações de problemas mentais das vítimas.
                    - Idade das vítimas.
                    - Gênero das vítimas.
                    ---------------------------------------
                    Como você deve agir
                    Você é um analista da Wisight, responsável por fornecer insights operacionais para o usuário, que atua em um departamento de polícia específico.
                    Seu objetivo é:
                    1. Identificar problemas que impactam diretamente o departamento.
                    2. Sugerir soluções práticas e de fácil aplicação no nível local.
                    3. Auxiliar o cliente a tomar decisões rápidas e informadas com base nos dados apresentados.
                    ---------------------------------------
                    Foco na dor do cliente
                    A principal preocupação do cliente está relacionada a: **"%s"**.
                    Com base nos dados fornecidos, ofereça insights que ajudem a entender:
                    - Quais fatores estão relacionados a esse tema no nível do departamento.
                    - Quais ações práticas podem ser implementadas para melhorar a situação.
                    ---------------------------------------
                    Formato esperado da resposta
                    Por favor, forneça as respostas no seguinte formato:
                    - Tópicos curtos e objetivos.
                    - Cada recomendação ou insight deve ser um tópico separado.
                    - O texto deve ser claro, direto e focado em ajudar o cliente a entender o problema e encontrar soluções.
                    Exemplo:
                    - Identifique padrões específicos relacionados ao tema "câmeras corporais".
                    - Recomende ações que possam ser implementadas de forma prática.
                    - Destaque fatores em comum que podem contribuir para a solução.
                    ---------------------------------------
                    Dados fornecidos
                    Abaixo estão as tuplas com informações do departamento. Use-as para embasar suas análises e recomendações.
                    """.formatted(keyWord);
        } else {
            return """
                    Contexto da Wisight
                    A Wisight é uma empresa especializada em criar soluções tecnológicas para empresas e governos.
                    Nosso objetivo é transformar dados em insights úteis que ajudem nossos clientes a resolver problemas específicos e melhorar operações.
                    ---------------------------------------
                    Sobre o Projeto
                    Este projeto utiliza relatórios policiais para fornecer uma visão operacional e analítica através de uma dashboard.
                    A dashboard exibe informações relevantes para o estado, como:
                    - Etnia das vítimas nos relatórios.
                    - Status das câmeras corporais (ligadas ou desligadas).
                    - Tentativas de fuga das vítimas (se ocorreram, como ocorreram).
                    - Se a vítima estava armada (e qual tipo de arma foi utilizada).
                    - Indicações de problemas mentais das vítimas.
                    - Idade das vítimas.
                    - Gênero das vítimas.
                    ---------------------------------------
                    Como você deve agir
                    Você é um analista da Wisight, responsável por fornecer insights estratégicos para um usuário que atua em um nível estadual.
                    Seu objetivo é:
                    1. Identificar problemas que impactam diretamente a operação em todo o estado.
                    2. Sugerir soluções práticas que possam ser aplicadas em diferentes regiões ou áreas administrativas.
                    3. Auxiliar o cliente a tomar decisões estratégicas baseadas em padrões identificados nos dados apresentados.
                    ---------------------------------------
                    Foco na dor do cliente
                    A principal preocupação do cliente está relacionada a: **"%s"**.  
                    Com base nos dados fornecidos, ofereça insights que ajudem a entender:
                    - Quais fatores estão relacionados a esse tema no nível estadual.
                    - Quais ações práticas podem ser implementadas para melhorar a situação em larga escala.
                    ---------------------------------------
                    Formato esperado da resposta
                    Por favor, forneça as respostas no seguinte formato:
                    - Tópicos curtos e objetivos.
                    - Cada recomendação ou insight deve ser um tópico separado.
                    - O texto deve ser claro, direto e focado em ajudar o cliente a entender o problema e encontrar soluções.
                    Exemplo:
                    - Identifique padrões específicos relacionados ao tema "câmeras corporais".
                    - Recomende ações que possam ser implementadas de forma prática.
                    - Destaque fatores em comum que podem contribuir para a solução.
                    ---------------------------------------
                    Dados fornecidos
                    Abaixo estão as tuplas com informações do estado. Use-as para embasar suas análises e recomendações.
                    """.formatted(keyWord);
        }
    }

    public HttpResponse<String> generateInsight(String data, String context) throws IOException, InterruptedException {
        String fullPrompt = context + data;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contents", new JSONObject[]{
                new JSONObject().put("parts", new JSONObject[]{
                        new JSONObject().put("text", fullPrompt)
                }).put("role", "user")
        });

        String jsonRequest = jsonObject.toString();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[DEBUG] Resposta recebida com status: " + response.statusCode());
        } catch (IOException | InterruptedException e) {
            System.out.println("[ERRO] Falha ao enviar requisição: " + e.getMessage());
            throw e;
        }
        processResponse(response);
        return response;
    }


    public String processResponse(HttpResponse<String> response) throws IOException {
        if (response == null || response.statusCode() != 200) {
            System.out.println("[ERRO] Resposta inválida ou status diferente de 200: " + (response != null ? response.statusCode() : "nulo"));
            System.out.println("[DEBUG] Corpo da resposta:\n" + (response != null ? response.body() : "nulo"));
            return null;
        }

        Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]+)\"");

        StringBuilder extractedText = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new StringReader(response.body()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) continue;
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    extractedText.append(matcher.group(1)).append(" ");
                }
            }
        } catch (Exception e) {
            System.out.println("[ERRO] Falha ao processar resposta: " + e.getMessage());
            throw e;
        }
        return extractedText.toString();
    }
}

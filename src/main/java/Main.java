import provider.DBConnectionProvider;

import java.io.IOException;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {


        String insightKey1 = args[0];
        String insightKey2 = args[1];
        String insightMessage = args[2];

        DBConnectionProvider provider = new DBConnectionProvider();

        DBOperations dbOperations = new DBOperations(
                provider.getDatabaseConnection(), insightKey1, insightKey2);

        String data = dbOperations.getData();

        System.out.println("Main, retorno String data: " + data);

        GeminiOperations geminiOperations = new GeminiOperations();

        String context = geminiOperations.getUserContextType(insightKey1, insightMessage);

        HttpResponse<String> response = geminiOperations.generateInsight(data, context);

        String insightText = geminiOperations.processResponse(response);

        dbOperations.insightToDatabase(insightText);

    }
}

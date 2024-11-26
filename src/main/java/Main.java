import config.Config;
import provider.DBConnectionProvider;

import java.io.IOException;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Config.getAllEnviroments();

        String insightKey = args[0];
        String insightMessage = args[1];

        DBConnectionProvider provider = new DBConnectionProvider();

        DBOperations dbOperations = new DBOperations(
                provider.getDatabaseConnection(), insightKey);

        String data = dbOperations.getData(insightKey);

        GeminiOperations geminiOperations = new GeminiOperations();

        String context = geminiOperations.getUserContextType(insightKey, insightMessage);

        HttpResponse<String> response = geminiOperations.generateInsight(data, context);

        String insightText = geminiOperations.processResponse(response);

        dbOperations.insightToDatabase(insightText);

    }
}

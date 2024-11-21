import config.Config;
import provider.DBConnectionProvider;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Config.getAllEnviroments();

        DBConnectionProvider provider = new DBConnectionProvider();

        DBOperations dbOperations = new DBOperations(
                provider.getDatabaseConnection(), Config.get("INSIGHT_KEY"));

        String data = dbOperations.getData(Config.get("INSIGHT_KEY"));

        GeminiOperations geminiOperations = new GeminiOperations();

        String context = geminiOperations.getUserContextType(Config.get("INSIGHT_KEY"));

        geminiOperations.generateInsight(data, context);




    }
}

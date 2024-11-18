import config.Config;
import provider.DBConnectionProvider;

public class Main {
    public static void main(String[] args) {

        DBConnectionProvider provider = new DBConnectionProvider();

        LoadToDB loadToDB = new LoadToDB(
                provider.getDatabaseConnection(), Config.get("INSIGHT_KEY"));

        loadToDB.load();



        String valueBodyServer = args[0];

        System.out.println("valor java: " + valueBodyServer);


    }
}

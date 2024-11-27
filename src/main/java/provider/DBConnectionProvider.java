package provider;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class DBConnectionProvider {

    private final JdbcTemplate databaseConnection;

    public DBConnectionProvider(){
        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setUrl("");
        basicDataSource.setDriverClassName("");
        basicDataSource.setUsername("");
        basicDataSource.setPassword("");

        databaseConnection = new JdbcTemplate(basicDataSource);
    }

    public JdbcTemplate getDatabaseConnection(){
        return databaseConnection;
    }
}

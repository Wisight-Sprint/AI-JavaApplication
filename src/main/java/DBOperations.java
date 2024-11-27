import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;


public class DBOperations {

    private final JdbcTemplate connection;
    private final String insightKey1;
    private final String insightKey2;
    private String data;

    public DBOperations(JdbcTemplate connection, String insightKey1, String insightKey2) {
        this.connection = connection;
        this.insightKey1 = insightKey1;
        this.insightKey2 = insightKey2;
    }

    public String getData() {
        StringBuilder result = new StringBuilder();
        String sqlQuery;

        if (insightKey1.chars().anyMatch(c -> Character.isDigit(c))) {
            Integer insightKeyId = Integer.valueOf(insightKey1);

            sqlQuery = "SELECT * FROM wisight.insight WHERE fk_departamento =" + insightKeyId;
        } else {
            Integer insightKeyId = connection.queryForObject("""
                            SELECT i.fk_cidade_estado FROM wisight.insight i 
                            JOIN cidade_estado c ON i.fk_cidade_estado = c.cidade_estado_id  
                            WHERE c.estado = ?""",
                    Integer.class, insightKey2);

            sqlQuery = "SELECT * FROM wisight.insights WHERE fk_cidade_estado = " + insightKeyId;
        }

        connection.query(sqlQuery, (rs, rowNum) -> {
            StringBuilder line = new StringBuilder();
            int columnCount = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) {
                    line.append(", ");
                }
                line.append(rs.getString(i));
            }

            if (result.length() > 0) {
                result.append(" | ");
            }
            result.append(line.toString());

            return null;
        });

        return result.toString();
    }

    public void insightToDatabase(String insightText) {
        LocalDateTime time = LocalDateTime.now();

        if (insightKey1.chars().anyMatch(c -> Character.isDigit(c))) {
            Integer insightId = connection.query(
                    "SELECT COALESCE(MAX(i.insight_id), 0) FROM wisight.insight i WHERE fk_departamento = ?",
                    (rs, rowNum) -> rs.getInt(1),
                    insightKey1
            ).stream().findFirst().orElse(0);

            insightId = insightId + 1;

            Integer newInsightKey = connection.queryForObject("""
                            SELECT i.fk_cidade_estado FROM wisight.insight i 
                            JOIN cidade_estado c ON i.fk_cidade_estado = c.cidade_estado_id  
                            WHERE c.estado = ?""",
                    Integer.class, insightKey2);


            connection.update("""
                    INSERT INTO insight VALUES (?, ?, ?, ?, ?)
                    """, insightId, time, insightText, newInsightKey, insightKey1);
        } else {
            Integer insightId = connection.query("""
                            SELECT COALESCE(MAX(i.insight_id), 0) FROM wisight.insight
                            JOIN cidade_estado c ON i.fk_cidade_estado = c.cidade_estado_id  
                            WHERE c.estado = ?""",
                    (rs, rowNum) -> rs.getInt(1),
                    insightKey1
            ).stream().findFirst().orElse(0);

            insightId = insightId + 1;


            Integer insightKeyId = connection.queryForObject("""
                            SELECT i.fk_cidade_estado FROM wisight.insight i 
                            JOIN cidade_estado c ON i.fk_cidade_estado = c.cidade_estado_id  
                            WHERE c.estado = ?""",
                    Integer.class, insightKey1);

            connection.update("""
                    INSERT INTO wisight.insight VALUES (?, ?, ?, ?, ?)
                    """, insightId, time, insightText, insightKeyId, insightKey2);
        }
    }
}
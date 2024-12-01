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

            sqlQuery = "SELECT * FROM wisight.relatorio WHERE fk_departamento = " + insightKeyId;
        } else {
            Integer insightKeyId = connection.queryForObject("""
                        SELECT c.cidade_estado_id FROM cidade_estado c
                        WHERE c.estado = ?""",
                    Integer.class, insightKey1);

            sqlQuery = """
                    SELECT * FROM wisight.relatorio r 
                    JOIN departamento d ON d.departamento_id = r.fk_departamento
                    JOIN cidade_estado c ON c.cidade_estado_id = d.fk_cidade_estado 
                    WHERE c.estado = """ + insightKey1;
        }

        connection.query(sqlQuery, (rs, rowNum) -> {
            StringBuilder line = new StringBuilder();
            int columnCount = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) {
                    line.append(", ");
                }
                String columnName = rs.getMetaData().getColumnName(i);
                String columnValue = rs.getString(i);
                line.append(columnName).append(": ").append(columnValue);
            }

            if (result.length() > 0) {
                result.append(" | ");
            }
            result.append(line.toString());

            return null;
        });

        System.out.println("DBOperations Data: " + result.toString());
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
                            SELECT d.fk_cidade_estado FROM wisight.departamento d
                            JOIN cidade_estado c ON c.cidade_estado_id = d.fk_cidade_estado
                            WHERE c.estado = ? AND d.departamento_id = ?
                            """,
                    Integer.class, insightKey2, insightKey1);

            System.out.println("Tupla ser inserida \n" +
                    "- InsightId: " + insightId + "\n" +
                    "- Time: " + time + "\n" +
                    "- InsightText: " + insightText + "\n" +
                    "- NewInsightKey: " + newInsightKey + "\n" +
                    "- InsightKey1: " + insightKey1 + "\n");

            connection.update("""
                    INSERT INTO wisight.insight VALUES (?, ?, ?, ?, ?)
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
                            SELECT c.cidade_estado_id FROM wisight.cidade_estado c
                            WHERE c.estado = ?""",
                    Integer.class, insightKey1);

            connection.update("""
                    INSERT INTO wisight.insight VALUES (?, ?, ?, ?, ?)
                    """, insightId, time, insightText, insightKeyId, insightKey2);
        }
    }
}
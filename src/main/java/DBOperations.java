import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;


public class DBOperations {

    private final JdbcTemplate connection;
    private final String insightKey;
    private String data;

    public DBOperations(JdbcTemplate connection, String insightKey) {
        this.connection = connection;
        this.insightKey = insightKey;
    }

    public String getData(String insightKey) {
        StringBuilder result = new StringBuilder();
        String sqlQuery;

        if (insightKey.chars().anyMatch(c -> Character.isDigit(c))) {
            Integer insightKeyId = Integer.valueOf(insightKey);

            sqlQuery = "SELECT * FROM insight WHERE fk_departamento =" + insightKeyId;
        } else {
            Integer insightKeyId = connection.queryForObject("""
                            SELECT i.fk_cidade_estado FROM insight i 
                            JOIN cidade_estado c ON i.fk_cidade_estado = c.cidade_estado_id  
                            WHERE c.estado = ?""",
                    Integer.class, insightKey);

            sqlQuery = "SELECT * FROM insights WHERE fk_cidade_estado = " + insightKeyId;
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

    public void specialUserInsert(String insightText) {
        Integer insightId = connection.queryForObject("""
                        SELECT i.insight_id FROM insight i 
                        JOIN cidade_estado c ON i.fk_cidade_estado = c.cidade_estado_id  
                        WHERE c.estado = ? ORDER BY insight_id DESC LIMIT 1""",
                Integer.class, insightKey);

        Integer insightKeyId = connection.queryForObject("""
                        SELECT i.fk_cidade_estado FROM insight i 
                        JOIN cidade_estado c ON i.fk_cidade_estado = c.cidade_estado_id  
                        WHERE c.estado = ?""",
                Integer.class, insightKey);
        insightId = (insightId == 0) ? 1 : (insightId + 1);

        LocalDateTime time = LocalDateTime.now();

        connection.update("""
                INSERT INTO insight VALUES (?, ?, ?, ?, ?)
                """, insightId, time, insightText, insightKeyId, null);
    }

    public void commonUserInsert(String insightText) {
        Integer insightId = connection.queryForObject(
                "SELECT i.insight_id FROM insight i WHERE fk_departamento = ? ORDER BY insight_id DESC LIMIT 1",
                Integer.class, insightKey);

        insightId = (insightId == 0) ? 1 : (insightId + 1);

        LocalDateTime time = LocalDateTime.now();

        connection.update("""
                INSERT INTO insight VALUES (?, ?, ?, ?, ?)
                """, insightId, time, insightText, null, insightKey);
    }
}
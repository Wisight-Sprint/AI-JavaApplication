import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

public class LoadToDB {

    private final JdbcTemplate connection;
    private final String insightKey;

    public LoadToDB(JdbcTemplate connection, String insightKey) {
        this.connection = connection;
        this.insightKey = insightKey;
    }

    public void load() {
        if (insightKey.chars().anyMatch(c -> Character.isDigit(c))){
            String insightText = generateInsightForDep();
            commonUserOperation(insightText);
        }
        else{
            String insightText = generateInsightForState();
            specialUserOperation(insightText);
        }
    }

    public String generateInsightForDep(){
        return "";
    }

    public String generateInsightForState(){
        return "";
    }

    public void specialUserOperation(String insightText) {
        Integer insightId = connection.queryForObject("""
                        SELECT i.insight_id FROM insight i 
                        JOIN cidade_estado c ON i.fk_cidade_estado = c.cidade_estado_id  
                        WHERE c.estado = ? ORDER BY insight_id DESC LIMIT 1""",
                Integer.class, insightKey);

        insightId = (insightId == 0) ? 1 : (insightId + 1);

        LocalDateTime time = LocalDateTime.now();

        connection.update("""
                INSERT INTO insight VALUES (?, ?, ?, ?, ?)
                """, insightId, time, insightText, insightKey, null);
    }

    public void commonUserOperation(String insightText) {
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
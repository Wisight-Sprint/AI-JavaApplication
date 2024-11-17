import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Teste {
    public static void main(String[] args) {
        String a = "SELECT i.insight_id from insight i ORDER BY i.insight_id DESC LIMIT 1;";

        String b = "a == 0 ? a = 1 : a+=1;";

        LocalDateTime now = LocalDateTime.now();
        String insertInsight = """
                    INSERT INTO insight VALUES 
                    (b, now, textoInsight, fkCidadeEstado, fkDepartamento);
                """;
    }
}

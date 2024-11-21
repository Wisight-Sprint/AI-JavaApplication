package model;

import java.time.LocalDateTime;

public class InsightModel {
    private Integer insight_id;
    private LocalDateTime dt_insercao;
    private String texto_insight;
    private Integer fk_cidade_estado;
    private Integer fk_departamento;


    public Integer getInsight_id() {
        return insight_id;
    }

    public void setInsight_id(Integer insight_id) {
        this.insight_id = insight_id;
    }

    public LocalDateTime getDt_insercao() {
        return dt_insercao;
    }

    public void setDt_insercao(LocalDateTime dt_insercao) {
        this.dt_insercao = dt_insercao;
    }

    public String getTexto_insight() {
        return texto_insight;
    }

    public void setTexto_insight(String texto_insight) {
        this.texto_insight = texto_insight;
    }

    public Integer getFk_cidade_estado() {
        return fk_cidade_estado;
    }

    public void setFk_cidade_estado(Integer fk_cidade_estado) {
        this.fk_cidade_estado = fk_cidade_estado;
    }

    public Integer getFk_departamento() {
        return fk_departamento;
    }

    public void setFk_departamento(Integer fk_departamento) {
        this.fk_departamento = fk_departamento;
    }
}

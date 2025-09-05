package estruturaDoProjeto.agendamendo;

import java.sql.Date;
import java.sql.Time;

public class Agendamento {
    private int id;
    private String cliente;
    private Date data;
    private Time hora;
    private DiaSemana diaSemana;
    private Status status;
    private String observacoes;

    // Getters e Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Date getData() {
        return data;
    }
    public void setData(Date data) {
        this.data = data;
    }

    public Time getHora() {
        return hora;
    }
    public void setHora(Time hora) {
        this.hora = hora;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }
    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public String toString() {
        return "Agendamento{" +
                "id=" + id +
                ", cliente='" + cliente + '\'' +
                ", data=" + data +
                ", hora=" + hora +
                ", diaSemana=" + diaSemana +
                ", status=" + status +
                ", observacoes='" + observacoes + '\'' +
                '}';
    }
}

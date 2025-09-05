package estruturaDoProjeto.agendamendo;

import estruturaDoProjeto.agendamendo.conexao.Conexao;
//import estruturaDoProjeto.conexao.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;

public class AgendamentoDAO {

    public void inserir(Agendamento agendamento) {
        String sql = "INSERT INTO agendamentos (cliente, data, hora, dia_semana, status, observacoes) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Conexao Conexao = null;
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, agendamento.getCliente());
            stmt.setDate(2, agendamento.getData());
            stmt.setTime(3, agendamento.getHora());
            stmt.setString(4, agendamento.getDiaSemana().name());
            stmt.setString(5, agendamento.getStatus().name());
            stmt.setString(6, agendamento.getObservacoes());

            stmt.executeUpdate();
            System.out.println("✅ Agendamento inserido com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizar(Agendamento agendamento) {
        String sql = "UPDATE agendamentos SET cliente=?, data=?, hora=?, dia_semana=?, status=?, observacoes=? " +
                "WHERE id=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, agendamento.getCliente());
            stmt.setDate(2, agendamento.getData());
            stmt.setTime(3, agendamento.getHora());
            stmt.setString(4, agendamento.getDiaSemana().name());
            stmt.setString(5, agendamento.getStatus().name());
            stmt.setString(6, agendamento.getObservacoes());
            stmt.setInt(7, agendamento.getId());

            stmt.executeUpdate();
            System.out.println("✅ Agendamento atualizado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM agendamentos WHERE id=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Agendamento deletado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Agendamento buscarPorId(int id) {
        String sql = "SELECT * FROM agendamentos WHERE id=?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearAgendamento(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Agendamento> listarTodos() {
        List<Agendamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM agendamentos ORDER BY data, hora";

        try (Connection conn = Conexao.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearAgendamento(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Agendamento mapearAgendamento(ResultSet rs) throws SQLException {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(rs.getInt("id"));
        agendamento.setCliente(rs.getString("cliente"));
        agendamento.setData(rs.getDate("data"));
        agendamento.setHora(rs.getTime("hora"));
        agendamento.setDiaSemana(DiaSemana.valueOf(rs.getString("dia_semana")));
        agendamento.setStatus(Status.valueOf(rs.getString("status")));
        agendamento.setObservacoes(rs.getString("observacoes"));
        return agendamento;
    }
}

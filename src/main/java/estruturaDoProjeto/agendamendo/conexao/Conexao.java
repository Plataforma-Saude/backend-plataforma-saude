package estruturaDoProjeto.agendamendo.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/agendamento_db";
    private static final String USUARIO = "root"; // altere se seu MySQL usar outro usuário
    private static final String SENHA = "root";   // altere para a senha correta

    public static Connection getConexao() throws SQLException {
        try {
            // Força carregamento do driver (em alguns casos é necessário)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado!", e);
        }
    }
}

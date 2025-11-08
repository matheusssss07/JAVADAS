package br.com.challenge.infrastructure.persistence.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    private static final String USERNAME = "RMxxxxxx";
    private static final String PASSWORD = "xxxxxx";

    private static Connection connection;


    private DatabaseConnection() {}


    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {

                Class.forName("oracle.jdbc.driver.OracleDriver");


                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Conexão com o banco estabelecida com sucesso!");

            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver JDBC não encontrado!", e);
            } catch (SQLException e) {
                System.err.println("Erro ao conectar com o banco: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }


    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexão com o banco fechada!");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }


    public static boolean testConnection() {
        try (Connection testConn = getConnection()) {
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            System.err.println("Falha no teste de conexão: " + e.getMessage());
            return false;
        }
    }
}
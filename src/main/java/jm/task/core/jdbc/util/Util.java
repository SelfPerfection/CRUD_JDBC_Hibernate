package jm.task.core.jdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util implements AutoCloseable{

    private static Util instance;
    private static Connection connection;

    private Util() throws SQLException {

        Properties props = new Properties();

        try (InputStream in = Util.class.getResourceAsStream("/db.config")) {
            props.load(in);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        String userName = props.getProperty("userName");
        String password = props.getProperty("password");
        String dbName = props.getProperty("dbName");
        String port = props.getProperty("port");
        String hostName = props.getProperty("hostName");
        String type = props.getProperty("type");

        String connectionURL = "jdbc:" + type + "://" + hostName + ":" + port + "/" + dbName + "?useSSL=false" + "&serverTimezone=UTC";
        connection = DriverManager.getConnection(connectionURL, userName, password);
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public static Util getInstance() throws SQLException {

        if (instance == null || instance.getConnection().isClosed()) {
            instance = new Util();
        }

        return instance;
    }

    @Override
    public void close() throws SQLException {
        instance.getConnection().close();
    }


}

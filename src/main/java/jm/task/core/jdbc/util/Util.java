package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Properties;

public class Util implements AutoCloseable {

    private static Util instance;
    private static Connection connection;
    private static SessionFactory sessionFactory;

    private Util() throws SQLException {

        Properties settings = loadDBSettings();

        String userName = settings.getProperty("userName");
        String password = settings.getProperty("password");
        String dbName = settings.getProperty("dbName");
        String port = settings.getProperty("port");
        String hostName = settings.getProperty("hostName");
        String type = settings.getProperty("type");

        String connectionURL = "jdbc:" + type + "://" + hostName + ":" + port + "/" + dbName + "?useSSL=false" + "&serverTimezone=UTC";
        connection = DriverManager.getConnection(connectionURL, userName, password);
    }

    private static Properties loadDBSettings() {
        Properties settings = new Properties();

        try (InputStream in = Util.class.getResourceAsStream("/db.config")) {
            settings.load(in);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return settings;
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

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Properties dbConfig = loadDBSettings();
                String connectionURL = "jdbc:" + dbConfig.getProperty("type") + "://"
                        + dbConfig.getProperty("hostName") + ":" + dbConfig.getProperty("port") + "/"
                        + dbConfig.getProperty("dbName") + "?useSSL=false" + "&serverTimezone=UTC";

                Configuration configuration = new Configuration();
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, dbConfig.getProperty("driver"));
                settings.put(Environment.URL, connectionURL);
                settings.put(Environment.USER, dbConfig.getProperty("userName"));
                settings.put(Environment.PASS, dbConfig.getProperty("password"));
                settings.put(Environment.DIALECT, dbConfig.getProperty("dialect"));
                settings.put(Environment.SHOW_SQL, "true");

                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                settings.put(Environment.HBM2DDL_AUTO, "create-drop");

                configuration.setProperties(settings);
                configuration.addAnnotatedClass(User.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    @Override
    public void close() throws SQLException {
        instance.getConnection().close();
    }
}
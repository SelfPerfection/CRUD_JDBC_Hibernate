package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

    public UserDaoJDBCImpl() {

    }

    public void createUsersTable() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS User (
                        id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
                        name varchar(255) NOT NULL,
                        lastName varchar(255),
                        age TINYINT
                    )
                    """;

        try (Util instance = Util.getInstance();
            Connection connection = instance.getConnection();
            Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
        } catch (SQLException e){
            printSQLException(e);
        }
    }

    public void dropUsersTable() {
        String sql = "DROP TABLE IF EXISTS User";

        try (Util instance = Util.getInstance();
            Connection connection = instance.getConnection();
            Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
        } catch (SQLException e){
            printSQLException(e);
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        String sql = "INSERT INTO `User` (`name`, `lastName`, `age`) VALUES (?, ?, ?)";

        try (Util instance = Util.getInstance();
            Connection connection = instance.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, lastName);
            statement.setInt(3, age);

            int resultStatement = statement.executeUpdate();

            if (resultStatement > 0 ) {
                System.out.println("User с именем – " + name + " добавлен в базу данных");
            }
        } catch (SQLException e){
            printSQLException(e);
        }
    }

    public void removeUserById(long id) {
        String sql = "DELETE FROM user WHERE id=?";

        try (Util instance = Util.getInstance();
            Connection connection = instance.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e){
            printSQLException(e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User";

        try (Util instance = Util.getInstance();
            Connection connection = instance.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {

            int i = 0;
            while (resultSet.next()) {
                User user = new User(resultSet.getString(2), resultSet.getString(3), resultSet.getByte(4));
                user.setId(resultSet.getLong(1));

                users.add(i, user);
                i++;
            }
        } catch (SQLException e){
            printSQLException(e);
        }

        return users;
    }

    public void cleanUsersTable() {
        String sql = "DELETE FROM User";

        try (Util instance = Util.getInstance();
            Connection connection = instance.getConnection();
            Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
        } catch (SQLException e){
            printSQLException(e);
        }
    }
}
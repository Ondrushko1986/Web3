package dao;

import executor.Executor;
import model.BankClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {

    private Connection connection;
    private Executor executor;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
        executor = new Executor(connection);
    }

    public List<BankClient> getAllBankClient() {
        List<BankClient> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet result = stmt.executeQuery("SELECT * FROM bank_client");
            while (result.next()) {
                list.add(new BankClient(result.getInt(1), result.getString(2),
                        result.getString(3), result.getLong(4)));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean validateClient(String name, String password) throws SQLException {
        if (getClientByName(name).getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    public boolean updateClientsMoney(String name, String password, Long transactValue) throws SQLException {
        if (validateClient(name, password)) {
            if (isClientHasSum(name, transactValue)) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE bank_client SET money = ? WHERE name = ?")){
                    preparedStatement.setLong(1,getClientByName(name).getMoney() + transactValue);
                    preparedStatement.setString(2,name);
                    preparedStatement.executeUpdate();
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public BankClient getClientById(long id) throws SQLException {
        return executor.execQuery("select * from bank_client where id=" + id, result -> {
            result.next();
            return new BankClient(result.getInt(1), result.getString(2),
                    result.getString(3), result.getLong(4));
        });
    } // +

    public boolean isClientHasSum(String name, Long expectedSum) { // +
        if (getClientByName(name).getMoney() >= Math.abs(expectedSum)) {
            return true;
        }
        return false;
    } // +

    public long getClientIdByName(String name) {
        try (Statement stmt = connection.createStatement();) {
            stmt.execute("select * from bank_client where name='" + name + "'");
            ResultSet result = stmt.getResultSet();
            while (result.next()) {
                Long id = result.getLong(1);
                result.close();
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    } // +

    public BankClient getClientByName(String name) {
        try {
            return executor.execQuery("select * from bank_client where name='" + name + "'", result -> {
                result.next();
                return new BankClient(result.getInt(1), result.getString(2),
                        result.getString(3), result.getLong(4));
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    } // +

    public boolean addClient(BankClient client) throws SQLException {
        if (getClientIdByName(client.getName()) <= 0) {
            executor.execUpdate("INSERT INTO bank_client"
                    + " (name, password, money) VALUES"
                    + " ('" + client.getName() + "', '" + client.getPassword() + "', " + client.getMoney() + ")");
            return true;
        }
        return false;
    } // +

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    } // +

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    } // +

    public Connection getConnection() {
        return connection;
    } // мой метод

    public void delClient(String name) throws SQLException {
        try (Statement stmt = connection.createStatement();) {
            stmt.execute("DELETE FROM bank_client WHERE name = '" + name + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BankClientService {

    public BankClientService() {
    }

    public BankClient getClientById(long id) throws DBException {
        try {
            return getBankClientDAO().getClientById(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientByNameAndPassword(String name, String password) {
        if (getBankClientDAO().validateClient(name, password)) {
            return getBankClientDAO().getClientByName(name);
        }
        return null;
    }

    public BankClient getClientByName(String name) {
        return getBankClientDAO().getClientByName(name);
    }

    public List<BankClient> getAllClient() {
        return getBankClientDAO().getAllBankClient();
    }

    public boolean deleteClient(String name) {
        try {
            getBankClientDAO().delClient(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addClient(BankClient client) throws DBException {
        BankClientDAO bankClientDAO = getBankClientDAO();
        boolean result = false;

        try {
            bankClientDAO.getConnection().setAutoCommit(false);
            createTable();
            if (bankClientDAO.addClient(client)) {
                result = true;
            }
            bankClientDAO.getConnection().commit();
        } catch (SQLException e) {

            try {
                bankClientDAO.getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {

            try {
                bankClientDAO.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) {
        BankClient bankClientTo;
        if (sender != null) {
            if ((bankClientTo = getBankClientDAO().getClientByName(name)) != null) {
                BankClientDAO bankClientDAO = getBankClientDAO();
                try {
                    bankClientDAO.getConnection().setAutoCommit(false);
                    if (getBankClientDAO().updateClientsMoney(sender.getName(), sender.getPassword(), value * -1)) {
                        if (getBankClientDAO().updateClientsMoney(name, bankClientTo.getPassword(), value)) {
                            bankClientDAO.getConnection().commit();
                            return true;
                        }
                    }
                } catch (SQLException e) {

                    try {
                        bankClientDAO.getConnection().rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    return false;
                } finally {

                    try {
                        bankClientDAO.getConnection().setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    public void cleanUp() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=root&").          //login
                    append("password=root");       //password

            System.out.println("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}

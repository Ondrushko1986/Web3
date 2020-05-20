package executor;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Executor {
    private Connection connection;

    public Executor(Connection connection) {
        this.connection = connection;
    }

    public <T> T execQuery(String query, ResultHandler<T> handler) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query);
        ResultSet resultSet = statement.getResultSet();
        T value = handler.handle(resultSet);
        resultSet.close();
        statement.close();
        return value;
    }

    public int execUpdate(String update) {
        try (Statement statement = connection.createStatement()){
            statement.execute(update);
            int updated = statement.getUpdateCount();
            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}

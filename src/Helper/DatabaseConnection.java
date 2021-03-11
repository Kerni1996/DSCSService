package Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private final String url = "jdbc:mysql://sql11.freesqldatabase.com:3306/sql11397975";
    private final String username = "sql11397975";
    private final String password = "tsMFhNz74c";

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties connectionProps = new Properties();
            connectionProps.put("user", this.username);
            connectionProps.put("password", this.password);
            this.connection = DriverManager.getConnection(url,username,password);
        } catch (ClassNotFoundException ex){
            System.out.println("Database Connection Creation Failed: " + ex.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnection getInstance() throws SQLException{
        if (instance == null) {
            instance = new DatabaseConnection();
        } else if (instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }

        return instance;
    }
}

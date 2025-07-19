import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:ecommerce.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS orders (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "order_id TEXT," +
                         "order_date TEXT," +
                         "customer_name TEXT," +
                         "region TEXT," +
                         "category TEXT," +
                         "sub_category TEXT," +
                         "product_name TEXT," +
                         "quantity INTEGER," +
                         "sales REAL," +
                         "profit REAL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


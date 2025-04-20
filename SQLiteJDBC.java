import java.sql.*;

public class SQLiteJDBC {

   public static void main(String args[]) {
      Connection c = null;
      Statement stmt = null;

      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:test.db");
         c.setAutoCommit(false);
         System.out.println("Opened database successfully");

         stmt = c.createStatement();

         String sql1 = "INSERT OR IGNORE INTO users (username, password) VALUES ('ananya', '123');";
         String sql2 = "INSERT OR IGNORE INTO users (username, password) VALUES ('surya', '123');";
         String sql3 = "INSERT OR IGNORE INTO users (username, password) VALUES ('admin', 'test');";
         String sql4 = "INSERT OR IGNORE INTO users (username, password) VALUES ('user', 'test');";

         stmt.executeUpdate(sql1);
         stmt.executeUpdate(sql2);
         stmt.executeUpdate(sql3);

         stmt.close();
         c.commit();
         c.close();

      } catch (Exception e) {
         System.err.println(e.getClass().getName() + ": " + e.getMessage());
         System.exit(0);
      }

      System.out.println("User records created successfully");
   }
}

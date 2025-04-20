import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class DBOps {

    // Insert Functions
    public static void insertProfessorsFromCSV(String csvFilePath, String dbUrl) {
        String insertSQL = "INSERT INTO professor (professor, course_title, course_code) VALUES (?, ?, ?)";

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(dbUrl);
                 PreparedStatement pstmt = conn.prepareStatement(insertSQL);
                 BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {

                br.readLine(); // Skip header

                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",", -1);
                    if (values.length < 3) continue;

                    pstmt.setString(1, values[0].trim());
                    pstmt.setString(2, values[1].trim());
                    pstmt.setString(3, values[2].trim());

                    pstmt.executeUpdate();
                }

                System.out.println("Professor data inserted.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertCoursesFromCSV(String csvFilePath, String dbUrl) {
        String insertSQL = """
            INSERT INTO courses (comp_code, course_no, course_title, L, P, U, sec, instructor, room, days, hours)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(dbUrl);
                 PreparedStatement pstmt = conn.prepareStatement(insertSQL);
                 BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {

                br.readLine(); // Skip header

                String line;
                while ((line = br.readLine()) != null) {
                    String[] v = line.split(",", -1);
                    if (v.length < 11) continue;

                    pstmt.setString(1, v[0].trim());
                    pstmt.setString(2, v[1].trim());
                    pstmt.setString(3, v[2].trim());
                    pstmt.setInt(4, Integer.parseInt(v[3].trim()));
                    pstmt.setInt(5, Integer.parseInt(v[4].trim()));
                    pstmt.setInt(6, Integer.parseInt(v[5].trim()));
                    pstmt.setString(7, v[6].trim());
                    pstmt.setString(8, v[7].trim());
                    pstmt.setString(9, v[8].trim());
                    pstmt.setString(10, v[9].trim());
                    pstmt.setString(11, v[10].trim());

                    pstmt.executeUpdate();
                }

                System.out.println("Courses inserted.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertRoomsFromCSV(String csvFilePath, String dbUrl) {
        String insertSQL = "INSERT INTO rooms (room, type, recording, location) VALUES (?, ?, ?, ?)";

        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(dbUrl);
                 PreparedStatement pstmt = conn.prepareStatement(insertSQL);
                 BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {

                br.readLine(); // Skip header

                String line;
                while ((line = br.readLine()) != null) {
                    String[] v = line.split(",", -1);
                    if (v.length < 4) continue;

                    pstmt.setString(1, v[0].trim());
                    pstmt.setString(2, v[1].trim());
                    pstmt.setString(3, v[2].trim());
                    pstmt.setString(4, v[3].trim());

                    pstmt.executeUpdate();
                }

                System.out.println("Rooms inserted.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create Table Functions
    public static void createProfessorTable(String dbUrl) {
        String sql = """
            CREATE TABLE IF NOT EXISTS professor (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                professor TEXT NOT NULL,
                course_title TEXT NOT NULL,
                course_code TEXT NOT NULL
            );
        """;

        executeCreate(dbUrl, sql);
    }

    public static void createCoursesTable(String dbUrl) {
        String sql = """
            CREATE TABLE IF NOT EXISTS courses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                comp_code TEXT NOT NULL,
                course_no TEXT NOT NULL,
                course_title TEXT NOT NULL,
                L INTEGER, P INTEGER, U INTEGER,
                sec TEXT, instructor TEXT, room TEXT,
                days TEXT, hours TEXT
            );
        """;

        executeCreate(dbUrl, sql);
    }

    public static void createRoomsTable(String dbUrl) {
        String sql = """
            CREATE TABLE IF NOT EXISTS rooms (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                room TEXT NOT NULL,
                type TEXT NOT NULL,
                recording TEXT NOT NULL,
                location TEXT NOT NULL
            );
        """;

        executeCreate(dbUrl, sql);
    }

    private static void executeCreate(String dbUrl, String createSQL) {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = DriverManager.getConnection(dbUrl);
                 Statement stmt = conn.createStatement()) {
                stmt.execute(createSQL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Read Functions
    public static void readProfessors(String dbUrl) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM professor")) {

            System.out.println("Professor Table:");
            while (rs.next()) {
                System.out.printf("ID: %d | Professor: %s | Title: %s | Code: %s%n",
                        rs.getInt("id"), rs.getString("professor"),
                        rs.getString("course_title"), rs.getString("course_code"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void readCourses(String dbUrl) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM courses")) {

            System.out.println("Courses Table:");
            while (rs.next()) {
                System.out.printf("ID: %d | %s %s | Title: %s | Room: %s | Instructor: %s%n",
                        rs.getInt("id"), rs.getString("comp_code"), rs.getString("course_no"),
                        rs.getString("course_title"), rs.getString("room"),
                        rs.getString("instructor"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void readRooms(String dbUrl) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms")) {

            System.out.println("Rooms Table:");
            while (rs.next()) {
                System.out.printf("ID: %d | Room: %s | Type: %s | Recording: %s | Location: %s%n",
                        rs.getInt("id"), rs.getString("room"),
                        rs.getString("type"), rs.getString("recording"), rs.getString("location"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Entry Point
    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:timetable.db";
        String professorCsv = "assets/professors.csv";
        String coursesCsv = "assets/courses.csv";
        String roomsCsv = "assets/rooms.csv";

        createProfessorTable(dbUrl);
        createCoursesTable(dbUrl);
        createRoomsTable(dbUrl);

        insertProfessorsFromCSV(professorCsv, dbUrl);
        insertCoursesFromCSV(coursesCsv, dbUrl);
        insertRoomsFromCSV(roomsCsv, dbUrl);

        readProfessors(dbUrl);
        readCourses(dbUrl);
        readRooms(dbUrl);
    }
}

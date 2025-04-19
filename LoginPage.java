import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {

    private boolean validateLogin(String username, String password) {
        String jdbcURL = "jdbc:sqlite:test.db";

        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(jdbcURL);

            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();
            boolean matched = result.next();

            result.close();
            statement.close();
            connection.close();

            return matched;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public LoginPage() {
        setTitle("Timetable Generator - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(238, 245, 233));
        backgroundPanel.setLayout(new GridBagLayout());
        add(backgroundPanel);

        JPanel loginPanel = new JPanel();
        loginPanel.setPreferredSize(new Dimension(400, 450));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setLayout(null);
        loginPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        backgroundPanel.add(loginPanel);

        JLabel titleLabel = new JLabel("Timetable Generator");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setBounds(80, 40, 300, 30);
        loginPanel.add(titleLabel);

        JLabel subtitle = new JLabel("Login to your account");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setBounds(120, 75, 200, 20);
        loginPanel.add(subtitle);

        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(50, 130, 100, 25);
        loginPanel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(50, 155, 300, 30);
        loginPanel.add(userText);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(50, 205, 100, 25);
        loginPanel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(50, 230, 300, 30);
        loginPanel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(50, 290, 300, 40);
        loginButton.setBackground(new Color(126, 125, 123));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginPanel.add(loginButton);

        JLabel statusLabel = new JLabel("");
        statusLabel.setBounds(50, 340, 300, 30);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(statusLabel);

        loginButton.addActionListener(e -> {
            String user = userText.getText();
            String pass = new String(passwordText.getPassword());

            if (validateLogin(user, pass)) {
                
                SwingUtilities.invokeLater(() -> {
                    new TimetableGeneratorUI().setVisible(true);
                });
                dispose(); 
            } else {
                statusLabel.setText("oops!");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });
    }
}

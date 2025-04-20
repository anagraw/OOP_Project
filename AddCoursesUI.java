import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AddCoursesUI extends JFrame {

    public AddCoursesUI() {
        setTitle("Timetable Generator");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(200, 220, 200));
        headerPanel.setPreferredSize(new Dimension(1000, 60));

        JLabel title = new JLabel("Timetable Generator");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(title, BorderLayout.WEST);

        JLabel userIcon = new JLabel("👤"); // Placeholder
        userIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        headerPanel.add(userIcon, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 250, 240));

        // Step Title
        JLabel stepTitle = new JLabel("Step 2: Add Courses");
        stepTitle.setFont(new Font("Arial", Font.BOLD, 20));
        stepTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 0));
        contentPanel.add(stepTitle);

        // Radio buttons and Upload
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton enterManually = new JRadioButton("Enter Manually", true);
        JRadioButton uploadCSV = new JRadioButton("Upload Courses CSV");
        ButtonGroup group = new ButtonGroup();
        group.add(enterManually);
        group.add(uploadCSV);
        JButton uploadButton = new JButton("Upload");

        optionsPanel.add(enterManually);
        optionsPanel.add(uploadCSV);
        optionsPanel.add(uploadButton);
        optionsPanel.setBackground(new Color(240, 250, 240));
        contentPanel.add(optionsPanel);

        // Table and Add Button
        String[] columns = {"#", "Course Name", "Department", "Professors", "TA", "Lecture Sections", "Lab Sections", "Capacity", "Action"};
        Object[][] data = {
                {1, "OOPS", "Computer Science", "Abhijeet Das", "Pranjali A", 1, 5, 80, "Edit"},
                {2, "OS", "Computer Science", "Barsha Mitra", "Madhu K", 1, "NA", 100, "Edit"},
                {3, "Analog Elec.", "Electronics and Elec.", "Ponnalagu N", "Raju B", 2, 5, 90, "Edit"},
                {4, "Power Elec.", "Electronics and Elec.", "Sudha Radhika", "Aditya M", 2, "NA", 85, "Edit"},
                {5, "MBFM", "Economics and Fin.", "Sunny Singh", "Pranjali A", 1, "NA", 80, "Edit"}
        };

        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(950, 200));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JButton addCourseButton = new JButton("+ Add Course");
        JPanel addBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addBtnPanel.add(addCourseButton);
        tablePanel.add(addBtnPanel, BorderLayout.NORTH);

        contentPanel.add(tablePanel);

        // Next Step Button
        JButton nextStep = new JButton("Next Step");
        nextStep.setBackground(new Color(0, 200, 0));
        nextStep.setForeground(Color.white);
        nextStep.setPreferredSize(new Dimension(120, 40));
        JPanel nextBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nextBtnPanel.add(nextStep);
        nextBtnPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        nextBtnPanel.setBackground(new Color(240, 250, 240));
        contentPanel.add(nextBtnPanel);

        add(contentPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddCoursesUI().setVisible(true));
    }
}

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AddCoursesUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton uploadButton;

    public AddCoursesUI() {
        setTitle("Timetable Generator");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(200, 220, 200));
        headerPanel.setPreferredSize(new Dimension(800, 60));

        JLabel title = new JLabel("Timetable Generator");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(title, BorderLayout.WEST);

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        headerPanel.add(userIcon, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 250, 240));

        JLabel stepTitle = new JLabel("Step 2: Add Courses");
        stepTitle.setFont(new Font("Arial", Font.BOLD, 20));
        stepTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 0));
        contentPanel.add(stepTitle);

        // Options
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton enterManually = new JRadioButton("Enter Manually", true);
        JRadioButton uploadCSV = new JRadioButton("Upload Courses CSV");
        ButtonGroup group = new ButtonGroup();
        group.add(enterManually);
        group.add(uploadCSV);
        uploadButton = new JButton("Upload");
        uploadButton.setVisible(false);

        uploadCSV.addActionListener(e -> uploadButton.setVisible(true));
        enterManually.addActionListener(e -> uploadButton.setVisible(false));
        uploadButton.addActionListener(e -> loadCSV());

        optionsPanel.add(enterManually);
        optionsPanel.add(uploadCSV);
        optionsPanel.add(uploadButton);
        optionsPanel.setBackground(new Color(240, 250, 240));
        contentPanel.add(optionsPanel);

        // Table setup
        String[] columns = {"COURSE NO.", "COURSE TITLE", "L", "P", "U", "INSTRUCTOR"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(750, 250));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JButton addCourseButton = new JButton("+ Add Course");
        addCourseButton.addActionListener(e -> openAddCourseDialog());

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

    private void loadCSV() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File csvFile = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                tableModel.setRowCount(0);
                String line;
                boolean firstLine = true;

                while ((line = br.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        line = line.replace("\uFEFF", ""); // BOM
                        continue;
                    }

                    String[] values = line.split(",");
                    if (values.length >= 8) {
                        for (int i = 0; i < values.length; i++) {
                            values[i] = values[i].trim();
                        }

                        tableModel.addRow(new Object[]{
                            values[1], // COURSE NO.
                            values[2], // COURSE TITLE
                            values[3], // L
                            values[4], // P
                            values[5], // U
                            values[7]  // INSTRUCTOR
                        });
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openAddCourseDialog() {
        JDialog dialog = new JDialog(this, "Add Course", true);
        dialog.setSize(500, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 5));
        String[] labels = {"COURSE NO.", "COURSE TITLE", "L", "P", "U", "INSTRUCTOR"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            formPanel.add(new JLabel(labels[i] + ":"));
            fields[i] = new JTextField();
            formPanel.add(fields[i]);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add");
        JButton cancelBtn = new JButton("Cancel");

        addBtn.addActionListener(e -> {
            String[] inputValues = new String[labels.length];
            for (int i = 0; i < fields.length; i++) {
                inputValues[i] = fields[i].getText().trim();
            }

            for (String val : inputValues) {
                if (val.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields.");
                    return;
                }
            }

            tableModel.addRow(inputValues);
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddCoursesUI().setVisible(true));
    }
}

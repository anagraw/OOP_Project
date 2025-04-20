import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class InstructorScheduleViewer extends JFrame {

    private JComboBox<String> instructorDropdown;
    private JTable scheduleTable;
    private Map<String, List<String[]>> instructorScheduleMap;

    public InstructorScheduleViewer(String filePath) {
        setTitle("Instructor Schedule Viewer");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        instructorScheduleMap = new HashMap<>();

        // Read and parse the CSV
        loadCSV(filePath);

        // Initialize UI
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Top panel for dropdown
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Instructor:"));

        instructorDropdown = new JComboBox<>(instructorScheduleMap.keySet().toArray(new String[0]));
        topPanel.add(instructorDropdown);
        add(topPanel, BorderLayout.NORTH);

        // Table to show schedule
        scheduleTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listener
        instructorDropdown.addActionListener(e -> updateTable());

        // Initial display
        if (instructorDropdown.getItemCount() > 0) {
            instructorDropdown.setSelectedIndex(0);
            updateTable();
        }
    }

    private void updateTable() {
        String selectedInstructor = (String) instructorDropdown.getSelectedItem();
        List<String[]> schedule = instructorScheduleMap.getOrDefault(selectedInstructor, new ArrayList<>());

        String[] columnNames = {"COURSE TITLE", "ROOM", "DAY", "HOUR"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (String[] row : schedule) {
            model.addRow(new String[]{row[0], row[1], row[2], row[3]});
        }

        scheduleTable.setModel(model);
    }


    private void loadCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirst = true;

            while ((line = br.readLine()) != null) {
                if (isFirst) {
                    isFirst = false; // Skip header
                    continue;
                }

                String[] fields = line.split(",", -1);

                if (fields.length < 11) continue;

                String instructor = fields[7].trim();
                String courseTitle = fields[2].trim();
                String room = fields[8].trim();
                String days = fields[9].trim().replaceAll("\"", "");
                String hours = fields[10].trim();

                String[] individualDays = days.split("\\s+");
                for (String day : individualDays) {
                    instructorScheduleMap
                            .computeIfAbsent(instructor, k -> new ArrayList<>())
                            .add(new String[]{courseTitle, room, day, hours});
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InstructorScheduleViewer("course.csv").setVisible(true);
        });
    }
}

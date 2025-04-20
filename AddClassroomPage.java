import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;

public class AddClassroomPage extends JFrame {
    private DefaultTableModel model;
    private JButton uploadButton;

    public AddClassroomPage() {
        setTitle("Generate Timetable Wizard - Add Classrooms");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set consistent font across all components
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("Table.rowHeight", 28);

        // -------- Top Panel --------
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(180, 205, 160));
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel appTitle = new JLabel("  Timetable Generator");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(appTitle, BorderLayout.WEST);

        JLabel profileIcon = new JLabel("👤");
        profileIcon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        profileIcon.setBorder(new EmptyBorder(0, 0, 0, 20));
        topPanel.add(profileIcon, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // -------- Main Panel --------
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel heading = new JLabel("Generate Timetable Wizard");
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel stepLabel = new JLabel("Step 1: Add Classrooms");
        stepLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        stepLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Timetable name
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setOpaque(false);
        namePanel.add(new JLabel("Timetable Name: "));
        JTextField timetableName = new JTextField(20);
        namePanel.add(timetableName);

        // Radio + upload
        JRadioButton manualOption = new JRadioButton("Enter Manually");
        JRadioButton uploadOption = new JRadioButton("Upload Classroom CSV");
        ButtonGroup bg = new ButtonGroup();
        bg.add(manualOption);
        bg.add(uploadOption);
        manualOption.setSelected(true);

        JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionPanel.setOpaque(false);
        optionPanel.add(manualOption);
        optionPanel.add(uploadOption);

        uploadButton = new JButton("Upload CSV");
        styleButton(uploadButton, new Color(255, 153, 51));
        uploadButton.setVisible(false);

        uploadOption.addActionListener(e -> uploadButton.setVisible(true));
        manualOption.addActionListener(e -> uploadButton.setVisible(false));

        uploadButton.addActionListener(e -> loadCSV());

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);

        // Table
        String[] columns = {"Room No", "Recording", "Computers", "Lab", "Description", "Action"};
        model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(204, 229, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(new Color(102, 153, 255));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1000, 250));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addClassroom = new JButton("+ Add Classroom");
        styleButton(addClassroom, new Color(102, 153, 255));
        addClassroom.setPreferredSize(new Dimension(200, 40));
        buttonPanel.add(addClassroom);

        JButton nextButton = new JButton("Next Step");
        styleButton(nextButton, new Color(0, 153, 76));
        buttonPanel.add(nextButton);

        addClassroom.setPreferredSize(nextButton.getPreferredSize());

        addClassroom.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            JTextField roomField = new JTextField();
            JComboBox<String> recordingBox = new JComboBox<>(new String[]{"Available", "NA"});
            JTextField computerField = new JTextField();
            JComboBox<String> labBox = new JComboBox<>(new String[]{"Yes", "No"});
            JTextField descField = new JTextField();

            inputPanel.add(new JLabel("Room No:"));
            inputPanel.add(roomField);
            inputPanel.add(new JLabel("Recording:"));
            inputPanel.add(recordingBox);
            inputPanel.add(new JLabel("Computers:"));
            inputPanel.add(computerField);
            inputPanel.add(new JLabel("Lab:"));
            inputPanel.add(labBox);
            inputPanel.add(new JLabel("Description:"));
            inputPanel.add(descField);

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Add Classroom", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int computerCount = Integer.parseInt(computerField.getText());
                    model.addRow(new Object[]{
                            roomField.getText(),
                            recordingBox.getSelectedItem(),
                            computerCount,
                            labBox.getSelectedItem(),
                            descField.getText(),
                            "Edit"
                    });
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number for Computers.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JLabel helpText = new JLabel("<html><i>Help: Add new classrooms by entering room details manually or upload a CSV file.</i></html>");
        helpText.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        // Add to Main Panel
        mainPanel.add(heading);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(stepLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(namePanel);
        mainPanel.add(optionPanel);
        mainPanel.add(uploadButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(helpText);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button, Color bg) {
        button.setFocusPainted(false);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(5, 15, 5, 15)
        ));
    }

    private void loadCSV() {
    JFileChooser fileChooser = new JFileChooser();
    int option = fileChooser.showOpenDialog(this);
    if (option == JFileChooser.APPROVE_OPTION) {
        File csvFile = fileChooser.getSelectedFile();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            model.setRowCount(0); // clear table
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    line = line.replace("\uFEFF", ""); // Remove BOM
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 4) {
                    for (int i = 0; i < 4; i++) {
                        values[i] = values[i].trim();
                    }
                    // Assuming order: Room, Type (Lab), Recording, Location
                    model.addRow(new Object[]{
                            values[0],         // Room No
                            values[2],         // Recording
                            "3",              // Computers (not present, set to NA or 0)
                            values[1],         // Lab (Type)
                            values[3],         // Description (Location)
                            "Edit"
                    });
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading CSV file.", "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(255, 204, 0));
            setForeground(Color.BLACK);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Edit");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(255, 204, 0));
            button.setForeground(Color.BLACK);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));

            button.addActionListener(e -> {
                fireEditingStopped();
                int row = table.getSelectedRow();
                if (row != -1) {
                    String currentDesc = (String) model.getValueAt(row, 4);
                    String updatedDesc = JOptionPane.showInputDialog(null, "Edit Description:", currentDesc);
                    if (updatedDesc != null) {
                        model.setValueAt(updatedDesc, row, 4);
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.table = table;
            label = (value == null) ? "Edit" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddClassroomPage().setVisible(true));
    }
}
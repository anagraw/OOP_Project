import javax.swing.*;
import java.awt.*;

public class TimetableGeneratorUI extends JFrame {

    public TimetableGeneratorUI() {
        setTitle("Timetable Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(214, 232, 204));
        JLabel titleLabel = new JLabel("Timetable Generator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search bar with dropdown
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setPreferredSize(new Dimension(getWidth(), 40));
        String[] timetables = {
                "My Timetable", "2024-25 Sem -1", "2023-24 Sem -2",
                "2023-24 Sem -1", "2022-23 Sem -2", "2022-23 Sem -1",
                "2021-22 Sem -2"
        };
        JComboBox<String> searchField = new JComboBox<>(timetables);
        searchField.setEditable(true);
        searchPanel.add(new JLabel("Search Timetable: "));
        searchPanel.add(searchField);

        // Generate button
        JButton generateButton = new JButton("Generate");
        headerPanel.add(generateButton, BorderLayout.EAST);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(230, 247, 210));
        contentPanel.setLayout(new GridLayout(3, 3, 20, 20));

        // Timetable cards
        for (String timetable : timetables) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            card.setBackground(Color.WHITE);
            card.setPreferredSize(new Dimension(150, 150));

            JLabel label = new JLabel(timetable, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 14));

            JButton viewEditButton = new JButton("View and Edit");
            viewEditButton.setBackground(Color.BLACK);
            viewEditButton.setForeground(Color.WHITE);
            viewEditButton.setFocusPainted(false);
            viewEditButton.setBorderPainted(false);

            viewEditButton.addActionListener(e -> {
                JFrame newFrame = new JFrame(timetable);
                newFrame.setSize(800, 500);
                newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                newFrame.add(new JLabel(timetable, SwingConstants.CENTER));
                newFrame.setVisible(true);
            });

            card.add(label, BorderLayout.CENTER);
            card.add(viewEditButton, BorderLayout.SOUTH);
            contentPanel.add(card);
        }

        // Add panels to the frame
        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TimetableGeneratorUI().setVisible(true);
        });
    }
}
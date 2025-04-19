import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.davidmoodie.SwingCalendar.Calendar;
import com.davidmoodie.SwingCalendar.CalendarEvent;
import com.davidmoodie.SwingCalendar.WeekCalendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class TimetableGeneratorUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TimetableGeneratorUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Timetable Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(214, 232, 204));
        JLabel titleLabel = new JLabel("Timetable Generator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search bar with dropdown
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setPreferredSize(new Dimension(frame.getWidth(), 40)); // Reduce space usage
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
            JPanel card = new JPanel();
            card.setLayout(new BorderLayout());
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

            viewEditButton.addActionListener(event -> {
                JFrame frm = new JFrame();

                ArrayList<CalendarEvent> events = new ArrayList<>();
                // Week of 14 Apr 2025
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 14), LocalTime.of(8, 0), LocalTime.of(8, 50),
                        "ECE F343 - T1 Tutorial\nI BLOCK I112"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 14), LocalTime.of(9, 0), LocalTime.of(9, 50),
                        "CS F213 - L1 Lecture\nF BLOCK F106"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 14), LocalTime.of(10, 0), LocalTime.of(10, 50),
                        "CS F372 - L1 Lecture\nF BLOCK F104"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 14), LocalTime.of(11, 0), LocalTime.of(11, 50),
                        "ECE F341 - L2 Lecture\nF BLOCK F104"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 14), LocalTime.of(12, 0), LocalTime.of(12, 50),
                        "ECE F343 - L1 Lecture\nF BLOCK F104"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 14), LocalTime.of(14, 0), LocalTime.of(14, 50),
                        "ECE F344 - L1 Lecture\nF BLOCK F106"));

                events.add(new CalendarEvent(LocalDate.of(2025, 4, 15), LocalTime.of(9, 0), LocalTime.of(9, 50),
                        "ECE F344 - L1 Lecture\nF BLOCK F106"));

                events.add(new CalendarEvent(LocalDate.of(2025, 4, 16), LocalTime.of(8, 0), LocalTime.of(8, 50),
                        "ECE F344 - T2 Tutorial\nI BLOCK I113"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 16), LocalTime.of(9, 0), LocalTime.of(9, 50),
                        "CS F213 - L1 Lecture\nF BLOCK F106"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 16), LocalTime.of(10, 0), LocalTime.of(10, 50),
                        "ECON F315 - L1 Lecture\nJ BLOCK J115"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 16), LocalTime.of(11, 0), LocalTime.of(12, 50),
                        "ECE F341 - P4 Laboratory\nJ BLOCK J106"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 16), LocalTime.of(12, 0), LocalTime.of(12, 50),
                        "ECE F343 - L1 Lecture\nF BLOCK F104"));

                events.add(new CalendarEvent(LocalDate.of(2025, 4, 17), LocalTime.of(9, 0), LocalTime.of(9, 50),
                        "ECE F344 - L1 Lecture\nF BLOCK F106"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 17), LocalTime.of(10, 0), LocalTime.of(10, 50),
                        "ECON F315 - L1 Lecture\nJ BLOCK J115"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 17), LocalTime.of(11, 0), LocalTime.of(12, 50),
                        "CS F213 - P2 Laboratory\nD BLOCK D311"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 17), LocalTime.of(14, 0), LocalTime.of(14, 50),
                        "ECE F341 - T2 Tutorial\nI BLOCK I122"));

                events.add(new CalendarEvent(LocalDate.of(2025, 4, 18), LocalTime.of(9, 0), LocalTime.of(9, 50),
                        "CS F213 - L1 Lecture\nF BLOCK F106"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 18), LocalTime.of(10, 0), LocalTime.of(10, 50),
                        "CS F372 - L1 Lecture\nF BLOCK F104"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 18), LocalTime.of(11, 0), LocalTime.of(11, 50),
                        "ECE F341 - L2 Lecture\nF BLOCK F104"));
                events.add(new CalendarEvent(LocalDate.of(2025, 4, 18), LocalTime.of(12, 0), LocalTime.of(12, 50),
                        "ECE F343 - L1 Lecture\nF BLOCK F104"));

                events.add(new CalendarEvent(LocalDate.of(2025, 4, 19), LocalTime.of(17, 0), LocalTime.of(17, 50),
                        "ECON F315 - L1 Lecture\nJ BLOCK J115"));

                WeekCalendar cal = new WeekCalendar(events);

                cal.addCalendarEventClickListener(e -> System.out.println(e.getCalendarEvent()));
                cal.addCalendarEmptyClickListener(e -> {
                    System.out.println(e.getDateTime());
                    System.out.println(Calendar.roundTime(e.getDateTime().toLocalTime(), 30));
                });

                JButton goToTodayBtn = new JButton("Today");
                goToTodayBtn.addActionListener(e -> cal.goToToday());

                JButton nextWeekBtn = new JButton(">");
                nextWeekBtn.addActionListener(e -> cal.nextWeek());

                JButton prevWeekBtn = new JButton("<");
                prevWeekBtn.addActionListener(e -> cal.prevWeek());

                JButton nextMonthBtn = new JButton(">>");
                nextMonthBtn.addActionListener(e -> cal.nextMonth());

                JButton prevMonthBtn = new JButton("<<");
                prevMonthBtn.addActionListener(e -> cal.prevMonth());

                JPanel weekControls = new JPanel();
                weekControls.add(prevMonthBtn);
                weekControls.add(prevWeekBtn);
                weekControls.add(goToTodayBtn);
                weekControls.add(nextWeekBtn);
                weekControls.add(nextMonthBtn);

                frm.add(weekControls, BorderLayout.NORTH);

                frm.add(cal, BorderLayout.CENTER);
                frm.setSize(1000, 900);
                frm.setVisible(true);
            });

            card.add(label, BorderLayout.CENTER);
            card.add(viewEditButton, BorderLayout.SOUTH);

            contentPanel.add(card);
        }

        // Add components to frame
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(searchPanel, BorderLayout.CENTER);
        frame.add(contentPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}

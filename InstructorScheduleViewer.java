import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import com.davidmoodie.SwingCalendar.Calendar;
import com.davidmoodie.SwingCalendar.CalendarEvent;
import com.davidmoodie.SwingCalendar.WeekCalendar;

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

                //    JFrame frm = new JFrame();

                //                 ArrayList<CalendarEvent> events = new ArrayList<>();
                //                 // Week of 14 Apr 2024
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 14), LocalTime.of(8, 0),
                //                                 LocalTime.of(8, 50),
                //                                 "ECE F343 - T1 Tutorial\nI BLOCK I112"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 14), LocalTime.of(9, 0),
                //                                 LocalTime.of(9, 50),
                //                                 "CS F213 - L1 Lecture\nF BLOCK F106"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 14), LocalTime.of(10, 0),
                //                                 LocalTime.of(10, 50),
                //                                 "CS F372 - L1 Lecture\nF BLOCK F104"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 14), LocalTime.of(11, 0),
                //                                 LocalTime.of(11, 50),
                //                                 "ECE F341 - L2 Lecture\nF BLOCK F104"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 14), LocalTime.of(12, 0),
                //                                 LocalTime.of(12, 50),
                //                                 "ECE F343 - L1 Lecture\nF BLOCK F104"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 14), LocalTime.of(14, 0),
                //                                 LocalTime.of(14, 50),
                //                                 "ECE F344 - L1 Lecture\nF BLOCK F106"));

                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 15), LocalTime.of(9, 0),
                //                                 LocalTime.of(9, 50),
                //                                 "ECE F344 - L1 Lecture\nF BLOCK F106"));

                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 16), LocalTime.of(8, 0),
                //                                 LocalTime.of(8, 50),
                //                                 "ECE F344 - T2 Tutorial\nI BLOCK I113"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 16), LocalTime.of(9, 0),
                //                                 LocalTime.of(9, 50),
                //                                 "CS F213 - L1 Lecture\nF BLOCK F106"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 16), LocalTime.of(10, 0),
                //                                 LocalTime.of(10, 50),
                //                                 "ECON F315 - L1 Lecture\nJ BLOCK J115"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 16), LocalTime.of(11, 0),
                //                                 LocalTime.of(12, 50),
                //                                 "ECE F341 - P4 Laboratory\nJ BLOCK J106"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 16), LocalTime.of(12, 0),
                //                                 LocalTime.of(12, 50),
                //                                 "ECE F343 - L1 Lecture\nF BLOCK F104"));

                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 17), LocalTime.of(9, 0),
                //                                 LocalTime.of(9, 50),
                //                                 "ECE F344 - L1 Lecture\nF BLOCK F106"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 17), LocalTime.of(10, 0),
                //                                 LocalTime.of(10, 50),
                //                                 "ECON F315 - L1 Lecture\nJ BLOCK J115"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 17), LocalTime.of(11, 0),
                //                                 LocalTime.of(12, 50),
                //                                 "CS F213 - P2 Laboratory\nD BLOCK D311"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 17), LocalTime.of(14, 0),
                //                                 LocalTime.of(14, 50),
                //                                 "ECE F341 - T2 Tutorial\nI BLOCK I122"));

                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 18), LocalTime.of(9, 0),
                //                                 LocalTime.of(9, 50),
                //                                 "CS F213 - L1 Lecture\nF BLOCK F106"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 18), LocalTime.of(10, 0),
                //                                 LocalTime.of(10, 50),
                //                                 "CS F372 - L1 Lecture\nF BLOCK F104"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 18), LocalTime.of(11, 0),
                //                                 LocalTime.of(11, 50),
                //                                 "ECE F341 - L2 Lecture\nF BLOCK F104"));
                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 18), LocalTime.of(12, 0),
                //                                 LocalTime.of(12, 50),
                //                                 "ECE F343 - L1 Lecture\nF BLOCK F104"));

                //                 events.add(new CalendarEvent(LocalDate.of(2024, 4, 19), LocalTime.of(17, 0),
                //                                 LocalTime.of(17, 50),
                //                                 "ECON F315 - L1 Lecture\nJ BLOCK J115"));

                //                 WeekCalendar cal = new WeekCalendar(events);

                //                 cal.addCalendarEventClickListener(e -> System.out.println(e.getCalendarEvent()));
                //                 cal.addCalendarEmptyClickListener(e -> {
                //                         System.out.println(e.getDateTime());
                //                         System.out.println(Calendar.roundTime(e.getDateTime().toLocalTime(), 30));
                //                 });

                //                 JButton goToTodayBtn = new JButton("Today");
                //                 goToTodayBtn.addActionListener(e -> cal.goToToday());

                //                 JButton nextWeekBtn = new JButton(">");
                //                 nextWeekBtn.addActionListener(e -> cal.nextWeek());

                //                 JButton prevWeekBtn = new JButton("<");
                //                 prevWeekBtn.addActionListener(e -> cal.prevWeek());

                //                 JButton nextMonthBtn = new JButton(">>");
                //                 nextMonthBtn.addActionListener(e -> cal.nextMonth());

                //                 JButton prevMonthBtn = new JButton("<<");
                //                 prevMonthBtn.addActionListener(e -> cal.prevMonth());

                //                 JPanel weekControls = new JPanel();
                //                 weekControls.add(prevMonthBtn);
                //                 weekControls.add(prevWeekBtn);
                //                 weekControls.add(goToTodayBtn);
                //                 weekControls.add(nextWeekBtn);
                //                 weekControls.add(nextMonthBtn);

                //                 frm.add(weekControls, BorderLayout.NORTH);

                //                 frm.add(cal, BorderLayout.CENTER);
                //                 frm.setSize(1000, 900);
                //                 frm.setVisible(true);
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

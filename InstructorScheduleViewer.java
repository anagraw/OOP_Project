import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import com.davidmoodie.SwingCalendar.Calendar;
import com.davidmoodie.SwingCalendar.CalendarEvent;
import com.davidmoodie.SwingCalendar.WeekCalendar;

import java.time.LocalDate;
import java.time.LocalTime;

public class InstructorScheduleViewer extends JFrame {

    private JComboBox<String> instructorDropdown;
    private JTable scheduleTable;
    private Map<String, List<String[]>> instructorScheduleMap;
    private JPanel calendarPanel;

    public InstructorScheduleViewer(String filePath) {
        setTitle("Instructor Schedule Viewer");
        setSize(1000, 700);
        // setDefaultCloseOperation(EXIT_ON_CLOSE);
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

        // Calendar Panel
        calendarPanel = new JPanel(new BorderLayout());
        add(calendarPanel, BorderLayout.CENTER);

        // Dropdown Action
        instructorDropdown.addActionListener(e -> updateCalendar());

        // Show first instructor's calendar by default
        if (instructorDropdown.getItemCount() > 0) {
            instructorDropdown.setSelectedIndex(0);
            updateCalendar();
        }
    }

   private void updateCalendar() {
    String selectedInstructor = (String) instructorDropdown.getSelectedItem();
    List<String[]> schedule = instructorScheduleMap.getOrDefault(selectedInstructor, new ArrayList<>());

    // Get current date and determine the Monday of the current week
    LocalDate today = LocalDate.now();
    LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1); // Monday = 1

    // Map day abbreviations to dates in the current week
    Map<String, LocalDate> dayToDate = new HashMap<>();
    dayToDate.put("M", monday);                  // Monday
    dayToDate.put("T", monday.plusDays(1));      // Tuesday
    dayToDate.put("W", monday.plusDays(2));      // Wednesday
    dayToDate.put("Th", monday.plusDays(3));     // Thursday
    dayToDate.put("F", monday.plusDays(4));      // Friday

    ArrayList<CalendarEvent> events = new ArrayList<>();

    for (String[] row : schedule) {
        String courseTitle = row[0];
        String room = row[1];
        String day = row[2];
        String hour = row[3];

        LocalDate date = dayToDate.get(day);
        if (date == null) continue;

        int hourSlot = Integer.parseInt(hour);
        LocalTime startTime = LocalTime.of(8 + (hourSlot - 1), 0);
        LocalTime endTime = startTime.plusMinutes(50);

        events.add(new CalendarEvent(date, startTime, endTime, courseTitle + "\n" + room));
    }

    calendarPanel.removeAll();

    WeekCalendar cal = new WeekCalendar(events);
    cal.addCalendarEventClickListener(e -> System.out.println(e.getCalendarEvent()));
    cal.addCalendarEmptyClickListener(e -> {
        System.out.println(e.getDateTime());
        System.out.println(Calendar.roundTime(e.getDateTime().toLocalTime(), 30));
    });

    calendarPanel.add(cal, BorderLayout.CENTER);
    calendarPanel.revalidate();
    calendarPanel.repaint();
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

            List<String> parsedHours = new ArrayList<>();
            int i = 0;
            while (i < hours.length()) {
                // Look ahead to check for '10'
                if (i + 1 < hours.length() && hours.substring(i, i + 2).equals("10")) {
                    parsedHours.add("10");
                    i += 2;
                } else {
                    char c = hours.charAt(i);
                    if (Character.isDigit(c)) {
                        parsedHours.add(String.valueOf(c));
                    }
                    i++;
                }
            }

            for (String day : individualDays) {
                for (String hour : parsedHours) {
                    instructorScheduleMap
                            .computeIfAbsent(instructor, k -> new ArrayList<>())
                            .add(new String[]{courseTitle, room, day, hour});
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}

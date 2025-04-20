import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors; // Added for reverse map creation

import com.davidmoodie.SwingCalendar.Calendar;
import com.davidmoodie.SwingCalendar.CalendarEvent;
import com.davidmoodie.SwingCalendar.WeekCalendar;

import java.time.LocalDate;
import java.time.LocalTime;

public class InstructorScheduleViewer extends JFrame {

    private JComboBox<String> instructorDropdown;
    // private JTable scheduleTable; // This was commented out in your original code
    private Map<String, List<String[]>> instructorScheduleMap;
    private JPanel calendarPanel;

    // --- Define the room swap mappings ---
    private static final Map<String, String> roomSwapMap = new HashMap<>();
    private static final Map<String, String> reverseRoomSwapMap; // For efficient reverse lookup

    static {
        // Define forward swaps
        roomSwapMap.put("F102", "F104");
        roomSwapMap.put("F103", "F106");
        roomSwapMap.put("F201", "G101");
        roomSwapMap.put("F202", "G102");
        roomSwapMap.put("F203", "G103");
        roomSwapMap.put("F204", "G204");
        roomSwapMap.put("F205", "G205");
        roomSwapMap.put("F206", "G206");

        // Create the reverse map automatically
        reverseRoomSwapMap = roomSwapMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }
    // --- End of swap map definition ---


    public InstructorScheduleViewer(String filePath) {
        setTitle("Instructor Schedule Viewer");
        setSize(1000, 700);
        // setDefaultCloseOperation(EXIT_ON_CLOSE); // Kept commented as in original
        setLocationRelativeTo(null);

        instructorScheduleMap = new HashMap<>();

        // Read and parse the CSV, applying swaps
        loadCSV(filePath);

        // Initialize UI
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Top panel for dropdown
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Instructor:"));

        // Ensure instructor names are sorted for the dropdown
        List<String> sortedInstructors = new ArrayList<>(instructorScheduleMap.keySet());
        Collections.sort(sortedInstructors);

        instructorDropdown = new JComboBox<>(sortedInstructors.toArray(new String[0]));
        topPanel.add(instructorDropdown);
        add(topPanel, BorderLayout.NORTH);

        // Calendar Panel
        calendarPanel = new JPanel(new BorderLayout());
        add(calendarPanel, BorderLayout.CENTER);

        // Dropdown Action
        instructorDropdown.addActionListener(e -> updateCalendar());

        // Show first instructor's calendar by default
        if (instructorDropdown.getItemCount() > 0) {
            instructorDropdown.setSelectedIndex(0); // updateCalendar will be called by this
        } else {
             // Handle case where no instructors were loaded (e.g., show empty calendar)
             updateCalendar();
        }
    }

    private void updateCalendar() {
        String selectedInstructor = (String) instructorDropdown.getSelectedItem();
        // Handle case where selection might be null if map is empty
        if (selectedInstructor == null) {
             selectedInstructor = ""; // Or handle as appropriate
        }

        List<String[]> schedule = instructorScheduleMap.getOrDefault(selectedInstructor, new ArrayList<>());

        // Get current date and determine the Monday of the current week
        LocalDate today = LocalDate.now();
        // Adjust to handle Sunday as day 7, Monday as day 1
        int dayOfWeekValue = today.getDayOfWeek().getValue(); // Monday=1, Sunday=7
        LocalDate monday = today.minusDays(dayOfWeekValue - 1);

        // Map day abbreviations to dates in the current week
        Map<String, LocalDate> dayToDate = new HashMap<>();
        dayToDate.put("M", monday);              // Monday
        dayToDate.put("T", monday.plusDays(1));  // Tuesday
        dayToDate.put("W", monday.plusDays(2));  // Wednesday
        dayToDate.put("Th", monday.plusDays(3)); // Thursday
        dayToDate.put("F", monday.plusDays(4));  // Friday
        // Add Saturday/Sunday if needed based on CSV data format
        // dayToDate.put("S", monday.plusDays(5)); // Saturday
        // dayToDate.put("Su", monday.plusDays(6)); // Sunday

        ArrayList<CalendarEvent> events = new ArrayList<>();

        for (String[] row : schedule) {
            // Ensure row has expected number of elements from loadCSV (should be 4)
             if (row.length < 4) continue;

            String courseTitle = row[0];
            String room = row[1]; // This is the potentially swapped room number
            String day = row[2];
            String hour = row[3];

            LocalDate date = dayToDate.get(day);
            if (date == null) continue; // Skip if day abbreviation is not mapped

            try {
                int hourSlot = Integer.parseInt(hour);
                // Assuming hour slots map directly to hours starting from 8 AM
                // Slot 1 -> 8:00, Slot 2 -> 9:00, ..., Slot 10 -> 17:00 (5 PM)
                if (hourSlot >= 1 && hourSlot <= 10) { // Adjust range if needed
                    LocalTime startTime = LocalTime.of(7 + hourSlot, 0); // Slot 1 = 8 AM
                    LocalTime endTime = startTime.plusMinutes(50); // Assuming 50 min classes

                    // Add event description including the (potentially swapped) room
                    events.add(new CalendarEvent(date, startTime, endTime, courseTitle + "\n" + room));
                } else {
                     System.err.println("Warning: Invalid hour slot '" + hour + "' for instructor " + selectedInstructor);
                }
            } catch (NumberFormatException nfe) {
                 System.err.println("Warning: Could not parse hour slot '" + hour + "' for instructor " + selectedInstructor);
                 continue; // Skip this entry if hour is not a valid number
            }
        }

        calendarPanel.removeAll(); // Clear previous calendar

        // Create and add the new calendar view
        WeekCalendar cal = new WeekCalendar(events);

        // Optional: Add listeners for clicks (as in original code)
        cal.addCalendarEventClickListener(e -> System.out.println("Event Clicked: " + e.getCalendarEvent()));
        cal.addCalendarEmptyClickListener(e -> {
            System.out.println("Empty Slot Clicked: " + e.getDateTime());
            // System.out.println(Calendar.roundTime(e.getDateTime().toLocalTime(), 30)); // Optional rounding
        });

        calendarPanel.add(cal, BorderLayout.CENTER);
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    // --- Helper function to apply bidirectional room swaps ---
    private String applyRoomSwaps(String originalRoom) {
        if (roomSwapMap.containsKey(originalRoom)) {
            return roomSwapMap.get(originalRoom); // Apply forward swap
        } else if (reverseRoomSwapMap.containsKey(originalRoom)) {
            return reverseRoomSwapMap.get(originalRoom); // Apply reverse swap
        }
        return originalRoom; // Return original if no swap is defined
    }
    // --- End of helper function ---


    private void loadCSV(String filePath) {
        instructorScheduleMap.clear(); // Clear previous data if loading again
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirst = true;

            while ((line = br.readLine()) != null) {
                if (isFirst) {
                    isFirst = false; // Skip header line
                     line = line.replace("\uFEFF", ""); // Remove BOM if present
                    continue;
                }

                String[] fields = line.split(",", -1); // Use -1 limit to keep trailing empty fields
                // Ensure enough columns exist based on expected CSV format
                if (fields.length < 11) {
                    System.err.println("Warning: Skipping malformed line (less than 11 columns): " + line);
                    continue;
                }

                // Trim fields to remove leading/trailing whitespace
                String instructor = fields[7].trim();
                String courseTitle = fields[2].trim();
                String originalRoom = fields[8].trim(); // Get the original room
                String days = fields[9].trim().replaceAll("\"", ""); // Remove potential quotes
                String hours = fields[10].trim().replaceAll("\"", ""); // Remove potential quotes

                // --- Apply the room swap logic ---
                String swappedRoom = applyRoomSwaps(originalRoom);
                // --- Room swap applied ---

                 // Skip if essential fields are empty
                 if (instructor.isEmpty() || courseTitle.isEmpty() || swappedRoom.isEmpty() || days.isEmpty() || hours.isEmpty()) {
                      System.err.println("Warning: Skipping line with empty essential fields: " + line);
                      continue;
                 }


                // Handle multiple days (e.g., "M W F", "T Th")
                 // Split by spaces, also handling potential multiple spaces
                String[] individualDays = days.split("\\s+");

                // Parse the concatenated hour string (e.g., "123", "910") into individual hours
                List<String> parsedHours = new ArrayList<>();
                int i = 0;
                while (i < hours.length()) {
                     // Handle "10" specifically as it's two characters
                     if (i + 1 < hours.length() && hours.substring(i, i + 2).equals("10")) {
                         parsedHours.add("10");
                         i += 2;
                     } else {
                         char c = hours.charAt(i);
                         // Add single digit hours (1-9)
                         if (Character.isDigit(c) && c != '0') { // Ensure it's a valid single digit 1-9
                             parsedHours.add(String.valueOf(c));
                         } else if (Character.isDigit(c) && c == '0') {
                              System.err.println("Warning: Found '0' in hour string, which is ambiguous. Skipping hour segment in line: " + line);
                         } else {
                             System.err.println("Warning: Non-digit character found in hour string. Skipping hour segment in line: " + line);
                         }
                         i++; // Move to the next character
                     }
                }

                // Add entry for each day and each hour parsed
                for (String day : individualDays) {
                    if (day.isEmpty()) continue; // Skip empty strings resulting from split
                    String trimmedDay = day.trim(); // Trim day just in case
                     // Validate day abbreviations if needed (e.g., M, T, W, Th, F)
                     if (!Arrays.asList("M", "T", "W", "Th", "F").contains(trimmedDay)) { // Add S, Su if applicable
                          System.err.println("Warning: Skipping unrecognized day '" + trimmedDay + "' in line: " + line);
                          continue;
                     }

                    for (String hour : parsedHours) {
                         // Add the schedule entry using the potentially swapped room
                        instructorScheduleMap
                                .computeIfAbsent(instructor, k -> new ArrayList<>())
                                .add(new String[]{courseTitle, swappedRoom, trimmedDay, hour}); // Use swappedRoom
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Error: Timetable file not found at\n" + filePath, "File Not Found", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading timetable file: " + e.getMessage(), "File Read Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
             // Catch unexpected errors during parsing
              e.printStackTrace();
              JOptionPane.showMessageDialog(this, "An unexpected error occurred while parsing the timetable file: " + e.getMessage(), "Parsing Error", JOptionPane.ERROR_MESSAGE);
        }

        // Optional: Log number of instructors loaded
        System.out.println("Loaded schedule data for " + instructorScheduleMap.size() + " instructors.");
    }

    // Example main method (if needed for standalone testing)
    // public static void main(String[] args) {
    //     // You would need to provide the path to your actual CSV file here
    //     String csvFilePath = "path/to/your/timetable_output.csv";
    //     SwingUtilities.invokeLater(() -> {
    //         new InstructorScheduleViewer(csvFilePath).setVisible(true);
    //     });
    // }

}
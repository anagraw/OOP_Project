import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter; // Might be useful for saving specific file types, though not strictly needed for directory selection
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// Assuming these are available in your project structure/dependencies
import com.davidmoodie.SwingCalendar.Calendar;
import com.davidmoodie.SwingCalendar.CalendarEvent;
import com.davidmoodie.SwingCalendar.WeekCalendar;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class InstructorScheduleViewer extends JFrame {

    private JComboBox<String> instructorDropdown;
    private Map<String, List<String[]>> instructorScheduleMap;
    private JPanel calendarPanel; // Panel containing the WeekCalendar
    private JButton exportButton;

    // --- Define the room swap mappings ---
    private static final Map<String, String> roomSwapMap = new HashMap<>();
    private static final Map<String, String> reverseRoomSwapMap;

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
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        instructorScheduleMap = new HashMap<>();

        // Read and parse the CSV, applying swaps
        loadCSV(filePath);

        // Initialize UI
        initUI();

        // Frame is made visible in the main method after initialization
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Top panel for controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Instructor:"));

        // Ensure instructor names are sorted for the dropdown
        List<String> sortedInstructors = new ArrayList<>(instructorScheduleMap.keySet());
        Collections.sort(sortedInstructors);

        instructorDropdown = new JComboBox<>(sortedInstructors.toArray(new String[0]));
        topPanel.add(instructorDropdown);

        // --- Add Export Button ---
        exportButton = new JButton("Export Timetable");
        topPanel.add(exportButton);
        // --- End Add Export Button ---

        add(topPanel, BorderLayout.NORTH);

        // Calendar Panel (where the WeekCalendar will be placed)
        calendarPanel = new JPanel(new BorderLayout());
        add(calendarPanel, BorderLayout.CENTER);

        // --- Add Action Listener for Export Button ---
        exportButton.addActionListener(e -> exportTimetableImage());
        // --- End Action Listener ---

        // Dropdown Action
        instructorDropdown.addActionListener(e -> updateCalendar());

        // Show first instructor's calendar by default
        if (instructorDropdown.getItemCount() > 0) {
            // Setting selected index will trigger the action listener and call updateCalendar()
            instructorDropdown.setSelectedIndex(0);
        } else {
             System.out.println("No instructors loaded. Showing empty calendar.");
             updateCalendar(); // Show an empty calendar view even if no data
        }
    }

     // Method to sanitize instructor name for use in filename
     private String sanitizeFilename(String name) {
         if (name == null) return "UnknownInstructor";
         // Replace characters potentially invalid in filenames with underscores
         return name.trim().replaceAll("[^a-zA-Z0-9\\.\\-_]+", "_");
     }

    // --- Method to Export Calendar Image (with Directory Chooser) ---
    private void exportTimetableImage() {
        String selectedInstructor = (String) instructorDropdown.getSelectedItem();
        if (selectedInstructor == null || selectedInstructor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an instructor first.", "Export Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ensure the calendarPanel is valid and has dimensions before attempting to capture
        // This check is important if the frame hasn't been displayed or is minimized
        if (calendarPanel.getWidth() <= 0 || calendarPanel.getHeight() <= 0 || !calendarPanel.isShowing()) {
             System.err.println("Calendar panel is not visible or has zero size. Width: " + calendarPanel.getWidth() + ", Height: " + calendarPanel.getHeight());
            JOptionPane.showMessageDialog(this,
             "Cannot export timetable. The calendar view is not currently visible or fully rendered.\nPlease ensure the window is visible and try again.",
             "Export Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Start File Chooser Logic ---
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setDialogTitle("Select Directory to Export Timetable");
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Allow selecting directories only
        directoryChooser.setAcceptAllFileFilterUsed(false); // Disable "All Files" option

        // Set default directory (optional, can start in user home or Downloads)
        Path defaultDir = Paths.get(System.getProperty("user.home"), "Downloads");
        if (Files.exists(defaultDir) && Files.isDirectory(defaultDir)) {
            directoryChooser.setCurrentDirectory(defaultDir.toFile());
        } else {
            directoryChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }

        // Show the directory chooser dialog
        int userSelection = directoryChooser.showSaveDialog(this); // showSaveDialog is semantically appropriate for 'saving to' a location

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File directoryToSave = directoryChooser.getSelectedFile();

            try {
                // 1. Create a BufferedImage of the calendarPanel's size
                BufferedImage image = new BufferedImage(
                        calendarPanel.getWidth(),
                        calendarPanel.getHeight(),
                        BufferedImage.TYPE_INT_ARGB // Use ARGB for transparency if needed
                );

                // 2. Get Graphics context and paint the panel onto the image
                Graphics2D g2d = image.createGraphics();
                // Use print() which is recommended for capturing the full component hierarchy accurately
                calendarPanel.print(g2d);
                g2d.dispose(); // Release graphics resources

                // 3. Prepare filename
                String sanitizedInstructor = sanitizeFilename(selectedInstructor);
                String dateStamp = LocalDate.now().toString(); // Format: YYYY-MM-DD
                String fileName = sanitizedInstructor + "_Timetable_" + dateStamp + ".png";

                // 4. Create the output file object using the selected directory
                File outputFile = new File(directoryToSave, fileName);

                // 5. Write the image to the file
                boolean success = ImageIO.write(image, "png", outputFile);

                // 6. Show confirmation or error message
                if (success) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Timetable exported successfully as:\n" + outputFile.getAbsolutePath(),
                            "Export Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                     System.out.println("Timetable exported to: " + outputFile.getAbsolutePath());
                } else {
                    // This might happen if no appropriate writer for "png" is found (very unlikely)
                    JOptionPane.showMessageDialog(this, "Failed to export timetable image (ImageIO writer failed).", "Export Error", JOptionPane.ERROR_MESSAGE);
                     System.err.println("ImageIO.write returned false.");
                }

            } catch (IOException ex) {
                // Handle errors during file operations (e.g., permission denied, disk full)
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "An error occurred while saving the image:\n" + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE
                );
                 System.err.println("IOException during image export: " + ex.getMessage());
            } catch (Exception ex) {
                 // Catch any other unexpected errors during capture/save
                 ex.printStackTrace();
                 JOptionPane.showMessageDialog(
                     this,
                     "An unexpected error occurred during export:\n" + ex.getMessage(),
                     "Export Error",
                     JOptionPane.ERROR_MESSAGE
                 );
                  System.err.println("Unexpected error during image export: " + ex.getMessage());
             }
        } else {
            // User cancelled the dialog
            System.out.println("Export cancelled by user.");
        }
        // --- End File Chooser Logic ---
    }
    // --- End Export Method ---


    private void updateCalendar() {
        String selectedInstructor = (String) instructorDropdown.getSelectedItem();
        if (selectedInstructor == null) {
            selectedInstructor = ""; // Use empty string if no instructor is selected/available
        }

        List<String[]> schedule = instructorScheduleMap.getOrDefault(selectedInstructor, new ArrayList<>());

        // Get current date and determine the Monday of the current week
        LocalDate today = LocalDate.now();
        int dayOfWeekValue = today.getDayOfWeek().getValue(); // 1=Monday, ..., 7=Sunday
        // Calculate the date of the current Monday. Adjust for Sunday if needed, but WeekCalendar typically starts Monday.
        LocalDate monday = today.minusDays(dayOfWeekValue - 1); // Correct for days M-Sat, works for Sunday too (7-1 = 6 days back)

        Map<String, LocalDate> dayToDate = new HashMap<>();
        dayToDate.put("M", monday);
        dayToDate.put("T", monday.plusDays(1));
        dayToDate.put("W", monday.plusDays(2));
        dayToDate.put("Th", monday.plusDays(3));
        dayToDate.put("F", monday.plusDays(4));
        // Add Sat and Sun if your data includes them and WeekCalendar supports it (it does, but layout is week view)
        // dayToDate.put("S", monday.plusDays(5));
        // dayToDate.put("Su", monday.plusDays(6));


        ArrayList<CalendarEvent> events = new ArrayList<>();

        for (String[] row : schedule) {
            // Expected format: [courseTitle, swappedRoom, day, hour]
            if (row.length < 4) {
                System.err.println("Skipping incomplete schedule entry: " + Arrays.toString(row));
                continue; // Skip if not enough data
            }

            String courseTitle = row[0];
            String room = row[1];
            String day = row[2];
            String hour = row[3]; // This is the hour slot number (1-10)

            LocalDate date = dayToDate.get(day);
            if (date == null) {
                 System.err.println("Skipping entry with unrecognized day: " + day + " in row: " + Arrays.toString(row));
                 continue; // Skip if day is not recognized (M, T, W, Th, F)
            }

            try {
                int hourSlot = Integer.parseInt(hour);
                // Assuming hour slots 1-10 correspond to times starting from 8:00 AM (7+1) to 5:00 PM (7+10)
                if (hourSlot >= 1 && hourSlot <= 10) {
                    LocalTime startTime = LocalTime.of(7 + hourSlot, 0); // 8:00 for slot 1, 9:00 for slot 2, etc.
                    LocalTime endTime = startTime.plusMinutes(50); // Assuming 50-minute slots

                    // Combine course title and room for the event description
                    String eventDescription = courseTitle + "\n" + room;
                    events.add(new CalendarEvent(date, startTime, endTime, eventDescription));
                } else {
                    System.err.println("Warning: Invalid hour slot '" + hour + "' for instructor " + selectedInstructor + ". Skipping event in row: " + Arrays.toString(row));
                }
            } catch (NumberFormatException nfe) {
                System.err.println("Warning: Could not parse hour slot '" + hour + "' for instructor " + selectedInstructor + ". Skipping event in row: " + Arrays.toString(row));
                 nfe.printStackTrace(); // Print stack trace for detailed debugging
                continue;
            }
        }

        // Remove the old calendar view and add the new one
        calendarPanel.removeAll();

        // WeekCalendar constructor takes a list of events
        WeekCalendar cal = new WeekCalendar(events);

        // Optional: Add listeners for clicks if you want interactivity
        cal.addCalendarEventClickListener(e -> System.out.println("Event Clicked: " + e.getCalendarEvent()));
        cal.addCalendarEmptyClickListener(e -> System.out.println("Empty Slot Clicked: " + e.getDateTime()));

        calendarPanel.add(cal, BorderLayout.CENTER); // Add the new calendar to the dedicated panel
        calendarPanel.revalidate(); // Tell the panel to re-layout its components
        calendarPanel.repaint(); // Tell the panel to repaint itself
    }

    // Helper function to apply bidirectional room swaps
    private String applyRoomSwaps(String originalRoom) {
        if (originalRoom == null || originalRoom.trim().isEmpty()) {
            return "N/A"; // Return "N/A" or similar for empty rooms
        }
        String trimmedRoom = originalRoom.trim();
        // Check forward swap
        if (roomSwapMap.containsKey(trimmedRoom)) {
            return roomSwapMap.get(trimmedRoom);
        }
        // Check reverse swap
        else if (reverseRoomSwapMap.containsKey(trimmedRoom)) {
            return reverseRoomSwapMap.get(trimmedRoom);
        }
        return trimmedRoom; // Return the original room if no swap is defined
    }


    private void loadCSV(String filePath) {
        instructorScheduleMap.clear(); // Clear previous data

        if (filePath == null || filePath.trim().isEmpty()) {
             JOptionPane.showMessageDialog(this, "CSV file path is not provided.", "Configuration Error", JOptionPane.ERROR_MESSAGE);
             System.err.println("CSV file path is null or empty.");
             return; // Exit if file path is invalid
        }

        File csvFile = new File(filePath);
        if (!csvFile.exists()) {
             JOptionPane.showMessageDialog(this, "Error: Timetable file not found at\n" + filePath, "File Not Found", JOptionPane.ERROR_MESSAGE);
             System.err.println("File not found: " + filePath);
             return; // Exit if file doesn't exist
        }
         if (!csvFile.isFile()) {
              JOptionPane.showMessageDialog(this, "Error: Provided path is not a file:\n" + filePath, "Invalid Path", JOptionPane.ERROR_MESSAGE);
              System.err.println("Provided path is not a file: " + filePath);
              return; // Exit if path is not a file
         }
         if (!csvFile.canRead()) {
              JOptionPane.showMessageDialog(this, "Error: Cannot read timetable file due to permissions:\n" + filePath, "Permission Error", JOptionPane.ERROR_MESSAGE);
              System.err.println("Cannot read file: " + filePath);
              return; // Exit if file is not readable
         }


        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isFirst = true; // Flag to skip header row

            while ((line = br.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                     // Attempt to remove BOM if present (for files saved with BOM in some editors)
                    line = line.replace("\uFEFF", "");
                    continue; // Skip header row
                }

                // Split the line by comma. Use -1 limit to keep trailing empty strings.
                // This is a basic split and might fail on CSVs with commas inside quoted fields.
                String[] fields = line.split(",", -1);
                if (fields.length < 11) {
                    System.err.println("Warning: Skipping malformed line (less than 11 columns): " + line);
                    continue; // Skip lines that don't have enough columns
                }

                // Extract data based on assumed column indices
                // Adjust these indices if your CSV columns are different
                String instructor = fields[7].trim();
                String courseTitle = fields[2].trim();
                String originalRoom = fields[8].trim();
                String days = fields[9].trim().replaceAll("\"", ""); // Remove quotes around days if present
                String hours = fields[10].trim().replaceAll("\"", ""); // Remove quotes around hours if present

                // Apply room swaps
                String swappedRoom = applyRoomSwaps(originalRoom);

                // Validate essential fields - skip entry if any core data is missing
                if (instructor.isEmpty() || courseTitle.isEmpty() || swappedRoom.isEmpty() || days.isEmpty() || hours.isEmpty()) {
                    System.err.println("Warning: Skipping line with empty essential fields: " + line);
                    continue;
                }

                // Split days (e.g., "M W F" -> ["M", "W", "F"]). Handles multiple spaces.
                String[] individualDays = days.split("\\s+");

                // Parse hours string (e.g., "12310" -> ["1", "2", "3", "10"])
                // This logic specifically handles '10' as a two-character hour slot.
                List<String> parsedHours = new ArrayList<>();
                int i = 0;
                while (i < hours.length()) {
                    if (i + 1 < hours.length() && hours.substring(i, i + 2).equals("10")) {
                        parsedHours.add("10");
                        i += 2; // Move index past "10"
                    } else {
                        char c = hours.charAt(i);
                        if (Character.isDigit(c) && c != '0') { // Accept digits 1-9
                            parsedHours.add(String.valueOf(c));
                        } else if (c == '0') {
                             System.err.println("Warning: Found '0' in hour string '" + hours + "' for instructor " + instructor + ". Skipping this character as '0' is not a valid slot number.");
                        } else {
                             System.err.println("Warning: Non-digit '" + c + "' found in hour string '" + hours + "' for instructor " + instructor + ". Skipping this character.");
                        }
                        i++; // Move index past the current character
                    }
                }

                // Create a schedule entry for each valid day and parsed hour slot
                for (String day : individualDays) {
                    if (day.isEmpty()) continue; // Skip empty strings resulting from split

                    String trimmedDay = day.trim();
                    // Basic validation for expected day formats
                    if (!Arrays.asList("M", "T", "W", "Th", "F").contains(trimmedDay)) {
                        System.err.println("Warning: Skipping unrecognized day '" + trimmedDay + "' in line: " + line);
                        continue; // Skip if day format is unexpected
                    }

                    for (String hour : parsedHours) {
                        // Add the entry to the map, using computeIfAbsent for efficient map updates
                        instructorScheduleMap
                                .computeIfAbsent(instructor, k -> new ArrayList<>())
                                .add(new String[]{courseTitle, swappedRoom, trimmedDay, hour});
                    }
                }
            }
             // Provide feedback if no data was loaded after processing
             if (instructorScheduleMap.isEmpty() && !isFirst) {
                  JOptionPane.showMessageDialog(this, "CSV file loaded but no valid schedule entries were found after parsing.", "No Data Loaded", JOptionPane.WARNING_MESSAGE);
                  System.out.println("CSV file processed, but no schedule entries parsed successfully.");
             } else if (isFirst) {
                   // If isFirst is still true, the file was likely empty or only contained the header
                   JOptionPane.showMessageDialog(this, "CSV file seems empty or only contains a header row.", "Empty File", JOptionPane.WARNING_MESSAGE);
                   System.out.println("CSV file seems empty or only contains a header.");
             }


        } catch (FileNotFoundException e) {
             // This is a fallback; ideally caught by file.exists()
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Timetable file not found at\n" + filePath, "File Not Found", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading timetable file: " + e.getMessage(), "File Read Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Catch any other unexpected errors during parsing
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred while parsing the timetable file: " + e.getMessage(), "Parsing Error", JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("Finished loading CSV. Data available for " + instructorScheduleMap.size() + " instructors.");
    }

    // Main method to run the application
    // public static void main(String[] args) {
    //     // --- IMPORTANT: Replace with the actual path to your CSV file ---
    //     String csvFilePath = "path/to/your/timetable_output.csv"; // e.g., "C:/Users/YourUser/Documents/timetable.csv"
    //     // ---------------------------------------------------------------

    //     // Use SwingUtilities.invokeLater to ensure the GUI is created and shown on the Event Dispatch Thread (EDT)
    //     SwingUtilities.invokeLater(() -> {
    //         InstructorScheduleViewer viewer = new InstructorScheduleViewer(csvFilePath);
    //         viewer.setVisible(true); // Make the frame visible
    //     });
    // }
}
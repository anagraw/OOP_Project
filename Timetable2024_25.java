import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class TimetableViewer {

    // PDF file path
    private static final String PDF_PATH = "/Users/anagraw/JAVA_OOP_Proj/TIMETABLE - II SEMESTER 2024 -25_removed.pdf";

    public static void main(String[] args) {
        // Parse PDF and get table data
        List<String[]> timetableData = parsePDF();

        // Column headers for the table
        String[] columnNames = {
                "Comp Code", "Course No.", "Course Title",
                "L", "P", "U", "Sec",
                "Instructor", "Room", "Days", "Hours", "Mid Sem", "End Sem"
        };

        // Convert List to 2D Array for JTable
        String[][] tableData = timetableData.toArray(new String[0][]);

        // Create Table Model and JTable
        DefaultTableModel model = new DefaultTableModel(tableData, columnNames);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Create Frame
        JFrame frame = new JFrame("Semester Timetable Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 600);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Function to parse PDF and return a list of rows (each as String array)
    private static List<String[]> parsePDF() {
        List<String[]> data = new ArrayList<>();

        try {
            PDDocument document = PDDocument.load(new File(PDF_PATH));
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            // Split the text by lines
            String[] lines = text.split("\\r?\\n");

            for (String line : lines) {
                // Skip empty lines and headings
                if (line.trim().isEmpty() || line.contains("COMP") || line.contains("TIMETABLE")) {
                    continue;
                }

                // Split line by spaces (or customize this based on your file structure)
                String[] parts = line.trim().split("\\s{2,}"); // split by 2+ spaces

                // If it's a valid row (likely has at least 8 fields)
                if (parts.length >= 8) {
                    // Fill missing columns if necessary
                    String[] row = new String[13];
                    for (int i = 0; i < Math.min(parts.length, 13); i++) {
                        row[i] = parts[i];
                    }
                    data.add(row);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading PDF: " + e.getMessage());
        }

        return data;
    }
}

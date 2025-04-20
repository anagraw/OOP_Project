Timetable Scheduler
===================

This is a Java Swing-based application designed to help generate and view timetables for university professors. 
It includes a login system, a calendar view of existing/past timetables, and a wizard to generate new ones based 
on classrooms and course data.

------------------------------------------------------------
HOW TO RUN
------------------------------------------------------------
1. Clone the repo
   ```sh
   git clone https://github.com/anagraw/OOP_Project.git
   ```

2. Compile the application (for Windows/Ubuntu)
   Open a terminal in the project directory and run:
   ```sh
   javac -cp ".;sqlite-jdbc-3.7.2.jar" LoginPage.java
   ```

2. Run the program
   ```sh
   java -cp ".;sqlite-jdbc-3.7.2.jar" LoginPage
   ```
   Note: You do NOT need to compile any other Java files.

------------------------------------------------------------
LOGIN CREDENTIALS
------------------------------------------------------------

Use one of the following username/password combinations:

   Username : Password
   --------------------
   surya    : 123, 
   ananya   : 123, 
   admin    : test

------------------------------------------------------------
APPLICATION FLOW
------------------------------------------------------------

1. Login Page
   Enter one of the above credentials.

2. Timetable Viewer
   After login, you can:
   - View existing or past timetables for multiple professors.
   - Click the "Generate" button to create a new timetable.

3. Timetable Wizard
   This step-by-step flow includes:
   
   a) Add Available Classrooms
      - Upload via CSV file (assets/rooms.csv), OR
      - Enter classroom data manually.

   b) Add Courses
      - Upload via CSV file (assets/Courses.csv), OR
      - Enter course data manually.

   c) Generate Timetable
      - The system uses the data to generate a complete timetable.

   d) Export as PNG
      - Once generated, the timetable can be exported to a .PNG file.

------------------------------------------------------------

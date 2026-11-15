DEMO DATA - 20 students (students_demo.csv)

How to use:
1. Run the project:  java -cp out app.StudentGUI
2. On the Dashboard click "Import from CSV".
3. Choose demo_data\students_demo.csv.

File format (first line is a header and is skipped):
Enrollment No, Admission No, Name, Program, Section, Marks1, Marks2, Marks3, Photo

- Photo is optional. To show a photo, put the image in demo_data\photos and write
  photos\filename.jpg in that column (path is relative to the CSV file).
- Rows with a duplicate enrollment number or invalid marks are skipped.
- Do not use commas inside any field.

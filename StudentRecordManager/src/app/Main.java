package app;

import model.Student;
import service.StudentManager;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static StudentManager manager = new StudentManager();

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    static int readMark(String prompt) {
        int m;
        do {
            m = readInt(prompt);
            if (m < 0 || m > 100) {
                System.out.println("Marks must be between 0 and 100.");
            }
        } while (m < 0 || m > 100);
        return m;
    }

    static String readText(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    static void addStudent() {
        int id = readInt("Enter ID: ");
        String name = readText("Enter name: ");
        String course = readText("Enter course: ");
        int[] marks = new int[Student.SUBJECTS];
        for (int i = 0; i < marks.length; i++) {
            marks[i] = readMark("Enter marks for subject " + (i + 1) + ": ");
        }
        if (manager.addStudent(new Student(id, name, course, marks))) {
            System.out.println("Student added successfully.");
        } else {
            System.out.println("A student with this ID already exists.");
        }
    }

    static void viewAll() {
        if (manager.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.println(manager.getReport());
    }

    static void searchStudent() {
        System.out.println("1. Search by ID   2. Search by name");
        int choice = readInt("Choice: ");
        if (choice == 1) {
            Student s = manager.search(readInt("Enter ID: "));
            if (s == null) {
                System.out.println("Student not found.");
            } else {
                System.out.println(manager.header());
                System.out.println(s);
            }
        } else if (choice == 2) {
            ArrayList<Student> found = manager.search(readText("Enter name: "));
            if (found.isEmpty()) {
                System.out.println("No matching students.");
            } else {
                System.out.println(manager.header());
                for (Student s : found) {
                    System.out.println(s);
                }
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    static void updateStudent() {
        Student s = manager.search(readInt("Enter ID to update: "));
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        int m1 = readMark("New marks for subject 1: ");
        int m2 = readMark("New marks for subject 2: ");
        int m3 = readMark("New marks for subject 3: ");
        if (s.updateMarks(m1, m2, m3)) {
            System.out.println("Marks updated.");
        } else {
            System.out.println("Update failed.");
        }
    }

    static void deleteStudent() {
        if (manager.delete(readInt("Enter ID to delete: "))) {
            System.out.println("Record deleted.");
        } else {
            System.out.println("Student not found.");
        }
    }

    static void showTopper() {
        Student top = manager.getTopper();
        if (top == null) {
            System.out.println("No records found.");
        } else {
            System.out.println("Topper: " + top.getName()
                    + " (Average: " + String.format("%.2f", top.getAverage()) + ")");
        }
    }

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n===== STUDENT RECORD MANAGEMENT =====");
            System.out.println("1. Add student");
            System.out.println("2. View all students");
            System.out.println("3. Search student");
            System.out.println("4. Update marks");
            System.out.println("5. Delete student");
            System.out.println("6. Sort by average (rank list)");
            System.out.println("7. Show topper");
            System.out.println("0. Exit");
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1: addStudent(); break;
                case 2: viewAll(); break;
                case 3: searchStudent(); break;
                case 4: updateStudent(); break;
                case 5: deleteStudent(); break;
                case 6:
                    manager.sortByAverage();
                    viewAll();
                    break;
                case 7: showTopper(); break;
                case 0: System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }
}

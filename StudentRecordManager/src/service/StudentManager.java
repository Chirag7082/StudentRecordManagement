package service;

import model.Student;
import java.util.ArrayList;

public class StudentManager {
    private ArrayList<Student> students = new ArrayList<>();

    public boolean addStudent(Student s) {
        if (search(s.getId()) != null) {
            Student.decrementCount(); // duplicate was rejected, undo the count
            return false; // duplicate ID
        }
        students.add(s);
        return true;
    }

    // Method overloading: search by ID
    public Student search(int id) {
        for (Student s : students) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    // Method overloading: search by name (partial, case-insensitive)
    public ArrayList<Student> search(String keyword) {
        ArrayList<Student> result = new ArrayList<>();
        String key = keyword.toLowerCase();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(key)) {
                result.add(s);
            }
        }
        return result;
    }

    // Search across name, enrollment no., admission no., program and section
    public ArrayList<Student> searchAny(String keyword) {
        ArrayList<Student> result = new ArrayList<>();
        String key = keyword.toLowerCase();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(key)
                    || s.getEnrollmentNo().toLowerCase().contains(key)
                    || s.getAdmissionNo().toLowerCase().contains(key)
                    || s.getCourse().toLowerCase().contains(key)
                    || s.getSection().toLowerCase().contains(key)) {
                result.add(s);
            }
        }
        return result;
    }

    public boolean enrollmentExists(String enrollmentNo, int exceptId) {
        for (Student s : students) {
            if (s.getId() != exceptId && s.getEnrollmentNo().equalsIgnoreCase(enrollmentNo)) {
                return true;
            }
        }
        return false;
    }

    public boolean delete(int id) {
        Student s = search(id);
        if (s == null) {
            return false;
        }
        students.remove(s);
        Student.decrementCount();
        return true;
    }

    // Bubble sort by average marks (highest first)
    public void sortByAverage() {
        int n = students.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (students.get(j).getAverage() < students.get(j + 1).getAverage()) {
                    Student temp = students.get(j);
                    students.set(j, students.get(j + 1));
                    students.set(j + 1, temp);
                }
            }
        }
    }

    public Student getTopper() {
        if (students.isEmpty()) {
            return null;
        }
        Student top = students.get(0);
        for (Student s : students) {
            if (s.getAverage() > top.getAverage()) {
                top = s;
            }
        }
        return top;
    }

    public ArrayList<Student> getAll() {
        return students;
    }

    public boolean isEmpty() {
        return students.isEmpty();
    }

    public String header() {
        return String.format("%-6s %-18s %-12s %4s %4s %4s %7s %7s  %s",
                "ID", "Name", "Course", "M1", "M2", "M3", "Total", "Avg", "Gr");
    }

    // StringBuffer used to build the full report
    public String getReport() {
        StringBuffer sb = new StringBuffer();
        sb.append(header()).append("\n");
        sb.append("-".repeat(78)).append("\n");
        for (Student s : students) {
            sb.append(s.toString()).append("\n");
        }
        sb.append("-".repeat(78)).append("\n");
        sb.append("Total students: ").append(Student.getCount());
        return sb.toString();
    }
}

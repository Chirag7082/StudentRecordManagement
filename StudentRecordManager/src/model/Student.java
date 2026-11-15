package model;

public class Student {
    public static final int SUBJECTS = 3;
    private static int count = 0;

    private int id;
    private String name;
    private String course;
    private int[] marks;
    private String enrollmentNo = "";
    private String admissionNo = "";
    private String section = "";
    private String photoPath = "";

    // Overloaded constructors
    public Student(int id, String name) {
        this(id, name, "B.Tech CSE", new int[SUBJECTS]);
    }

    public Student(int id, String name, String course, int[] marks) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.marks = marks;
        count++;
    }

    // Full constructor used by the GUI (profile card details)
    public Student(int id, String name, String course, int[] marks,
                   String enrollmentNo, String admissionNo, String section, String photoPath) {
        this(id, name, course, marks);
        this.enrollmentNo = enrollmentNo;
        this.admissionNo = admissionNo;
        this.section = section;
        this.photoPath = photoPath;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCourse() { return course; }
    public int[] getMarks() { return marks; }

    public String getEnrollmentNo() { return enrollmentNo; }
    public String getAdmissionNo() { return admissionNo; }
    public String getSection() { return section; }
    public String getPhotoPath() { return photoPath; }

    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }
    public void setAdmissionNo(String admissionNo) { this.admissionNo = admissionNo; }
    public void setSection(String section) { this.section = section; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public void setName(String name) { this.name = name; }
    public void setCourse(String course) { this.course = course; }

    // Variable-length arguments
    public boolean updateMarks(int... newMarks) {
        if (newMarks.length != SUBJECTS) {
            return false;
        }
        for (int m : newMarks) {
            if (m < 0 || m > 100) {
                return false;
            }
        }
        marks = newMarks;
        return true;
    }

    public int getTotal() {
        int total = 0;
        for (int m : marks) {
            total += m;
        }
        return total;
    }

    public double getAverage() {
        return (double) getTotal() / marks.length;
    }

    public char getGrade() {
        double avg = getAverage();
        if (avg >= 90) return 'A';
        else if (avg >= 75) return 'B';
        else if (avg >= 60) return 'C';
        else if (avg >= 40) return 'D';
        else return 'F';
    }

    public static int getCount() { return count; }

    public static void decrementCount() { count--; }

    @Override
    public String toString() {
        return String.format("%-6d %-18s %-12s %4d %4d %4d %7d %7.2f  %c",
                id, name, course, marks[0], marks[1], marks[2],
                getTotal(), getAverage(), getGrade());
    }
}

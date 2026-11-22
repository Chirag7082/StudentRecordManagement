# Student Record Management System

A console-based Java application to manage student records (add, view, search, update, delete, rank list, topper).

## Team
| Name | Roll No. | Contribution |
|------|----------|--------------|
| (Chirag Sharma) |250160203050 | Student class |
| (Nikhil Yadav) | | StudentManager: add, search, delete |
| (Aryan sah) | | Sorting, topper, report |
| (Annant gupta) | | Main menu, input validation, documentation |

## Features
- Add, view, search (by ID or name), update marks, delete students
- Rank list sorted by average marks
- Topper display, automatic total, average and grade

## Java Concepts Used
- **Unit 1:** switch menu, loops (for, for-each, do-while), operators, if-else
- **Unit 2:** classes and objects, constructors (overloaded, `this`), static members, access control, method overloading, varargs, Scanner
- **Unit 3:** arrays, ArrayList, String methods, StringBuffer, packages

## Project Structure
```
src/model/Student.java
src/service/StudentManager.java
src/app/Main.java
src/app/StudentGUI.java
run.bat
StudentRecordManager.jar
demo_data/students_demo.csv
docs/   (report, class diagram, screenshots)
```

## Quick Start (Windows, JDK 11 or newer installed)
- Double-click **run.bat** (compiles and opens the window version), or
- Double-click **StudentRecordManager.jar**
- Demo data: use the *Import from CSV* button and pick `demo_data/students_demo.csv`

## How to Run from the command line (JDK 11 or newer)
```
javac -d out src/model/*.java src/service/*.java src/app/*.java
java -cp out app.Main        (console version)
java -cp out app.StudentGUI  (window version)
```

## Screenshots
Add screenshots of the running program in the `docs` folder.

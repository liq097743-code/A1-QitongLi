# A1 Medical Appointment System — Interactive Java Version

## Run in VS Code

Open this folder in VS Code. With JDK 17, Language Support for Java by Red Hat and Debugger for Java installed, open
src/AssignmentOne.java and select Run above main. Click inside the TERMINAL panel,
type a menu number, then press Enter. Input cannot be entered in the Output panel.
If VS Code asks for a console, choose the integrated terminal.

The program starts with no appointments. Each prompt expects one line of input.
Invalid input prints a warning and returns to the menu. Select 0 to exit.
Appointments are held in memory for this session and are not saved after exit.

## Menu

1. View staff and today's availability.
2. Add a booking: patient name, phone, staff ID, and HH:mm start time.
3. List all bookings in chronological order.
4. Filter by staff ID, preserving chronological order.
5. Filter by patient phone, preserving chronological order.
6. Cancel one booking by staff ID and start time.
7. Cancel all bookings for a patient phone.
0. Exit and display the final bookings.

Example: choose 2, then enter Taylor, 0400000001, 101 and 09:00 on separate lines.
Choose 3 to view the booking. Repeat the same booking to see a duplicate warning.

## Business rules and design

Only today's appointments are accepted. Slots start every 30 minutes from 08:00
through 15:30; the clinic closes at 16:00. One professional cannot have two bookings
at the same time. Different professionals may share a time slot. Weekly working days
are validated. IDs contain digits and normalize leading zeroes; phones contain 7–15
digits and preserve leading zeroes.

MedicalProfessional is abstract, with private fields and defensive schedule copies.
Its four subclasses supply role-specific information. Patient validates mutable
contact details; Appointment stores a defensive snapshot and numeric minutes.
AppointmentManager handles storage, queries, standard sorting and cancellations.
AssignmentOne handles the console menu and input only. English Javadoc and inline
comments explain the design. No HTML or JavaScript is required.

## Compile and test with JDK 17

javac -encoding UTF-8 -Xlint:all -d out src/*.java tests/*.java
java -cp out AssignmentOne
java -cp out AppointmentSystemTest

The original 84 model checks are retained. The interactive entry point replaces the
previous automatic demonstration. Existing slides about automatically loading five
bookings do not describe this version's startup; enter the demonstration data first.

The .vscode folder identifies src/tests as source folders and configures an integrated terminal. Use Run and Debug > Run A1 - Interactive Menu, or press Ctrl+F5. Wait for Java project import to finish first.


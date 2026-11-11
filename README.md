# A1 Medical Appointment System

A Java 17 medical appointment system with clinical staff profiles, daily
bookings, chronological queries and cancellation. The project is named
`A1-QitongLi`.

## Design

- An abstract `MedicalProfessional` encapsulates numeric identity, name and a
  fixed weekly schedule. Four subclasses supply distinct clinical details.
- Identity is based on the numeric staff identifier, including across separate
  objects. Leading zeroes are removed, so `00101` and `101` identify one person.
- Appointments belong to the manager's single service date. The default is today.
  Times use `LocalTime`, not lexicographic text sorting.
- 08:00 is inclusive and 16:00 is the closing time (exclusive). Appointments start
  every 30 minutes; the last start is 15:30.
- A professional must work on the service day's weekday. Weekly days are copied
  on input and output to protect the object's state.
- Arrays and loops store, filter and insertion-sort appointments. Returned arrays
  are independent snapshots. Appointment objects and professional data are immutable.
- Cancelling by patient phone removes all matching appointments that day. A phone
  is a digit-only string (7–15 digits); leading zeroes are preserved.
- Each immutable booking stores the patient's name and phone directly.
  Constructors require all mandatory information.

## Build and run (PowerShell, JDK 17 on PATH)

On this computer, run `powershell -ExecutionPolicy Bypass -File ./run.ps1` for the
demonstration, or add `-Test` for the regression checks. The script also detects
the portable JDK installed under `%LOCALAPPDATA%/CodexJava` during development.

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -encoding UTF-8 -Xlint:all -d out src/*.java tests/*.java
java -cp out AssignmentOne
java -cp out AppointmentSystemTest
javadoc -quiet -Xdoclint:all -d docs/api src/*.java
```

`AssignmentOne.main` contains six labelled demonstration sections. Demonstration
staff work Monday–Sunday so the example succeeds on any current weekday. More
restricted weekly schedules are covered by tests.

## Validation

Compiled with Java 17 using `-Xlint:all`; all 74 regression checks passed.
Javadoc generated successfully with `-Xdoclint:all`. `demo-output.txt` contains
the actual demonstration output from development. The tests cover both time
boundaries, invalid fields, off-duty staff, identity, conflicts, filtering,
sorting, cancellation, defensive copying and array growth.

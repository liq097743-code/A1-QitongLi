# A1 Medical Appointment System

Java 17 console application. Main entry point: src/AssignmentOne.java.

## Run in VS Code

1. Install JDK 17 and the Microsoft Extension Pack for Java in VS Code.
2. Open this project folder in VS Code and trust it if prompted.
3. Open src/AssignmentOne.java.
4. Click Run above the main method to display the six-part console demo.

The demo runs automatically; it does not prompt for keyboard input.
To run regression checks, open tests/AppointmentSystemTest.java and run its main method.
## Design

- MedicalProfessional is abstract. Its private fields and defensive weekly schedule
  copies protect identity and availability. Four concrete clinical roles implement
  roleDetails. Three general practitioners and three other professionals are displayed.
- Patient has private name/mobilePhone fields and validated getters/setters.
  Appointment takes defensive patient snapshots so later contact changes cannot
  silently change booking queries or cancellation keys.
- Appointment stores numeric minutes since midnight and implements Comparable.
  LocalDate, LocalTime and DayOfWeek remain as date/time boundary types.
- ClinicSchedule centralizes opening, closing and interval constants. Opening is
  inclusive; closing is exclusive. Current slots run from 08:00 through 15:30.
- AppointmentManager can only be constructed for today and rejects other dates.
  A manager left open past midnight rejects new bookings; create a new one for today.
- Standard Collections.sort sorts independent query results, including filtered
  results. Booking storage uses a dynamically expanded array.
- Staff identity uses numeric strings, normalizing leading zeroes. Phone numbers
  preserve zeroes and require 7 to 15 digits. Cancelling by phone removes all matches.
- Staff and bookings intentionally have no mutation setters: changing identity,
  time or availability after registration could invalidate existing bookings.
  Create replacement objects and cancel/rebook instead.
- The source uses no Lambda, Stream, GUI or Java file IO. Tests also use ordinary
  try/catch instead of Lambda or Runnable.

## Verification

84 regression checks passed, including patient snapshots, invalid setter atomicity,
invalid filters, staff identity, slot boundaries, working days, sorting, cancellation
and storage growth. Compilation with -Xlint:all and Javadoc with -Xdoclint:all pass.
Run AssignmentOne to display the six-part console demo.

With JDK 17 on PATH:

```text
javac -encoding UTF-8 -Xlint:all -d out src/*.java tests/*.java
java -cp out AssignmentOne
java -cp out AppointmentSystemTest
javadoc -quiet -Xdoclint:all -d docs/api src/*.java
```


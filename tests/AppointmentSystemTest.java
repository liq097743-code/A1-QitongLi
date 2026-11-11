import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/** Dependency-free regression checks; failures throw even without JVM -ea. */
public final class AppointmentSystemTest {
    private static int checks;
    private AppointmentSystemTest() { }

    /** @param args unused */
    public static void main(String[] args) {
        LocalDate day = LocalDate.of(2026, 11, 9); // A fixed Monday makes tests reproducible.
        DayOfWeek[] schedule = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY};
        MedicalProfessional gp = new GeneralPractitioner("001", "Doctor", schedule, "Family medicine");
        MedicalProfessional duplicate = new GeneralPractitioner("1", "Doctor", schedule, "Family medicine");
        MedicalProfessional dentist = new Dentist("2", "Dentist", schedule, "Restorative dentistry");
        check(gp.equals(duplicate) && gp.hashCode() == duplicate.hashCode(), "Numeric identity");
        schedule[0] = DayOfWeek.FRIDAY;
        gp.getWorkingDays()[0] = DayOfWeek.FRIDAY;
        check(gp.worksOn(DayOfWeek.MONDAY) && !gp.worksOn(DayOfWeek.FRIDAY), "Defensive schedule copies");
        AppointmentManager manager = new AppointmentManager(day);
        check(manager.add(booking(gp, day, 14, 0)), "Add late booking");
        check(manager.add(booking(gp, day, 8, 0)), "Add opening slot");
        check(!manager.add(booking(duplicate, day, 8, 0)), "Reject same ID via different object");
        check(manager.add(booking(dentist, day, 8, 0)), "Allow different staff at same time");
        check(manager.add(booking(dentist, day, 15, 30)), "Accept last slot");
        check(!manager.add(booking(gp, day.plusDays(1), 9, 0)), "Reject different date");
        check(!manager.add(null), "Reject missing booking");
        ordered(manager.getAll());
        ordered(manager.findByStaffId("001"));
        ordered(manager.findByPatientPhone("0400000001"));
        check(manager.findByStaffId("1").length == 2, "Filter ID");
        check(manager.findByStaffId("999").length == 0, "Empty filter");
        Appointment[] snapshot = manager.getAll();
        snapshot[0] = null;
        check(manager.getAll()[0] != null, "Independent results");
        check(manager.cancelByStaffAndTime("001", LocalTime.of(8, 0)), "Cancel staff and time");
        check(!manager.cancelByStaffAndTime("1", LocalTime.of(8, 0)), "Cancel nonexistent slot");
        check(manager.cancelByPatientPhone("0400000001") == 3, "Remove all phone matches");
        check(manager.getAll().length == 0, "All removed");
        check(manager.cancelByPatientPhone("0400000001") == 0, "Missing phone feedback");
        check(!manager.cancelByStaffAndTime("bad", LocalTime.of(8, 0)), "Invalid cancellation ID");
        invalid(() -> booking(gp, day, 7, 30));
        invalid(() -> booking(gp, day, 16, 0));
        invalid(() -> booking(gp, day, 8, 15));
        invalid(() -> new Appointment("A", "0400000001", day, LocalTime.of(8, 0, 1), gp));
        invalid(() -> new Appointment("A", "0400000001", day, LocalTime.of(8, 0, 0, 1), gp));
        invalid(() -> new Appointment(" ", "0400000001", day, LocalTime.of(8, 0), gp));
        invalid(() -> new Appointment("A", "invalid", day, LocalTime.of(8, 0), gp));
        invalid(() -> new Appointment("A", "0400000001", day, null, gp));
        invalid(() -> new Appointment("A", "0400000001", null, LocalTime.of(8, 0), gp));
        invalid(() -> new Appointment("A", "0400000001", day, LocalTime.of(8, 0), null));
        invalid(() -> booking(gp, day.plusDays(2), 8, 0));
        invalid(() -> new Nurse("x", "N", DayOfWeek.values(), "RN"));
        invalid(() -> new Nurse("3", "N", new DayOfWeek[0], "RN"));
        invalid(() -> new Nurse("3", "N", new DayOfWeek[]{null}, "RN"));
        invalid(() -> new Nurse("3", "N", new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.MONDAY}, "RN"));
        invalid(() -> new Nurse("3", "N", DayOfWeek.values(), ""));
        // Exceed initial array capacity and check all sixteen valid daily slots survive.
        for (int i = 15; i >= 0; i--) {
            check(manager.add(booking(gp, day, 8 + i / 2, (i % 2) * 30)), "Array growth booking " + i);
        }
        check(manager.getAll().length == 16, "Storage grows");
        ordered(manager.getAll());
        check(manager.cancelByPatientPhone("0400000001") == 16, "Remove adjacent matches after growth");
        System.out.println("PASS: " + checks + " checks");
    }

    private static Appointment booking(MedicalProfessional staff, LocalDate date, int hour, int minute) {
        return new Appointment("Patient", "0400000001", date, LocalTime.of(hour, minute), staff);
    }

    private static void ordered(Appointment[] bookings) {
        for (int i = 1; i < bookings.length; i++) {
            check(bookings[i - 1].getTime().compareTo(bookings[i].getTime()) <= 0, "Chronological order");
        }
    }

    private static void invalid(Runnable action) {
        try { action.run(); }
        catch (IllegalArgumentException expected) { checks++; return; }
        throw new AssertionError("Expected invalid input to be rejected");
    }

    private static void check(boolean condition, String label) {
        checks++;
        if (!condition) { throw new AssertionError(label); }
    }
}

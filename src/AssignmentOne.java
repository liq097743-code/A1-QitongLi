import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/** Entry point demonstrating staff profiles and the appointment workflow. */
public final class AssignmentOne {
    private AssignmentOne() { }

    /** @param args unused command-line arguments */
    public static void main(String[] args) {
        // Part 1: common identity and a fixed weekly schedule.
        System.out.println("\nPART 1 - Common staff information");
        DayOfWeek[] days = DayOfWeek.values();
        MedicalProfessional gp = new GeneralPractitioner("101", "Alex Chen", days, "Family medicine");
        System.out.println("ID: " + gp.getId() + ", name: " + gp.getName());

        // Part 2: four concrete types with different clinical information.
        System.out.println("\nPART 2 - Four clinical roles");
        MedicalProfessional dentist = new Dentist("102", "Sam Lee", days, "Orthodontics");
        MedicalProfessional physio = new Physiotherapist("103", "Robin Tan", days, "Sports rehabilitation");
        MedicalProfessional nurse = new Nurse("104", "Jamie Wu", days, "Registered nurse");
        MedicalProfessional[] staff = {gp, dentist, physio, nurse};
        System.out.println("Created " + staff.length + " types of clinical staff.");

        // Part 3: one base-type loop invokes each subclass's clinical details.
        System.out.println("\nPART 3 - Polymorphic staff display");
        for (MedicalProfessional professional : staff) { System.out.println(professional); }

        // Part 4: immutable appointments accept all staff types.
        System.out.println("\nPART 4 - Create appointments for today");
        AppointmentManager manager = new AppointmentManager();
        LocalDate today = manager.getServiceDate();
        Appointment[] bookings = {
            new Appointment("Taylor", "0400000001", today, LocalTime.of(14, 0), gp),
            new Appointment("Morgan", "0400000002", today, LocalTime.of(8, 0), dentist),
            new Appointment("Taylor", "0400000001", today, LocalTime.of(9, 0), gp),
            new Appointment("Casey", "0400000003", today, LocalTime.of(8, 0), physio),
            new Appointment("Jordan", "0400000004", today, LocalTime.of(15, 30), nurse)
        };
        System.out.println(bookings[0]);

        // Part 5: deliberately unsorted insertion, queries and both cancellation methods.
        System.out.println("\nPART 5 - Manage appointments");
        for (Appointment booking : bookings) { manager.add(booking); }
        display("All bookings sorted by time", manager.getAll());
        display("Staff 101 sorted by time", manager.findByStaffId("101"));
        display("Patient 0400000001 sorted by time", manager.findByPatientPhone("0400000001"));
        manager.cancelByStaffAndTime("102", LocalTime.of(8, 0));
        display("After staff/time cancellation", manager.getAll());
        manager.cancelByPatientPhone("0400000001");
        display("After phone cancellation", manager.getAll());

        // Part 6: expected failures leave the remaining valid bookings intact.
        System.out.println("\nPART 6 - Error handling");
        MedicalProfessional samePhysio = new Physiotherapist("00103", "Robin Tan", days, "Sports rehabilitation");
        System.out.println("Separate objects, same staff identity: " + physio.equals(samePhysio));
        manager.add(new Appointment("Duplicate", "0400000005", today, LocalTime.of(8, 0), samePhysio));
        manager.add(new Appointment("Tomorrow", "0400000005", today.plusDays(1), LocalTime.of(10, 0), gp));
        manager.add(null);
        manager.cancelByStaffAndTime("999", LocalTime.of(10, 0));
        manager.cancelByPatientPhone("0499999999");
        try { new Appointment("", "0400000006", today, LocalTime.of(10, 0), gp); }
        catch (IllegalArgumentException exception) { System.out.println("WARNING: " + exception.getMessage()); }
        try { new Appointment("Invalid time", "0400000006", today, LocalTime.of(16, 0), gp); }
        catch (IllegalArgumentException exception) { System.out.println("WARNING: " + exception.getMessage()); }
        try { new Appointment("Invalid phone", "abc", today, LocalTime.of(10, 0), gp); }
        catch (IllegalArgumentException exception) { System.out.println("WARNING: " + exception.getMessage()); }
        try { new Nurse("A104", "Invalid ID", days, "Registered nurse"); }
        catch (IllegalArgumentException exception) { System.out.println("WARNING: " + exception.getMessage()); }
        display("Final bookings after failed operations", manager.getAll());
    }

    private static void display(String title, Appointment[] appointments) {
        System.out.println("\n" + title + " (" + appointments.length + ")");
        if (appointments.length == 0) { System.out.println("No appointments found."); }
        for (Appointment appointment : appointments) { System.out.println(appointment); }
    }
}

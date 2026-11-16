import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.DateTimeException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Console entry point for manually managing today's medical appointments. */
public final class AssignmentOne {
    private AssignmentOne() { }

    /**
     * Runs the interactive menu until the user exits or input ends.
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        // Part 1: shared identities and fixed weekly schedules.
        DayOfWeek[] everyDay = DayOfWeek.values();
        // Part 2: four clinical roles, including three general practitioners.
        MedicalProfessional[] staff = {
            new GeneralPractitioner("101", "Alex Chen", everyDay, "Family medicine"),
            new GeneralPractitioner("106", "Chris Wong",
                    new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY},
                    "Preventive care"),
            new GeneralPractitioner("107", "Dana Lin",
                    new DayOfWeek[]{DayOfWeek.TUESDAY, DayOfWeek.THURSDAY}, "Chronic care"),
            new Dentist("102", "Sam Lee", everyDay, "Orthodontics"),
            new Physiotherapist("103", "Robin Tan", everyDay, "Sports rehabilitation"),
            new Nurse("104", "Jamie Wu", everyDay, "Registered nurse")
        };
        AppointmentManager manager = new AppointmentManager();
        System.out.println("A1 Medical Appointment System - Interactive Java Version");
        System.out.println("Bookings are for today only: " + manager.getServiceDate());
        System.out.println("Opening hours: 08:00-16:00. Last start: 15:30. Slots: 30 minutes.");
        System.out.println("This session starts empty. Bookings are not saved after exit.");
        // Part 3: polymorphic output includes each role's professional details.
        displayStaff(staff, manager);
        try (Scanner input = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                try {
                    String choice = read(input, "Choose an option (0-7): ");
                    switch (choice) {
                        case "1":
                            displayStaff(staff, manager);
                            break;
                        case "2":
                            // Part 4: collect patient input and create a validated booking.
                            addBooking(input, staff, manager);
                            break;
                        case "3":
                            display("All bookings sorted by time", manager.getAll());
                            break;
                        case "4":
                            display("Staff bookings sorted by time",
                                    manager.findByStaffId(read(input, "Staff ID: ")));
                            break;
                        case "5":
                            display("Patient bookings sorted by time",
                                    manager.findByPatientPhone(read(input, "Patient phone: ")));
                            break;
                        case "6":
                            // Part 5: cancellation is delegated to the appointment manager.
                            String id = read(input, "Staff ID: ");
                            LocalTime time = readTime(input);
                            manager.cancelByStaffAndTime(id, time);
                            display("Bookings after cancellation", manager.getAll());
                            break;
                        case "7":
                            manager.cancelByPatientPhone(read(input, "Patient phone (cancel all matches): "));
                            display("Bookings after cancellation", manager.getAll());
                            break;
                        case "0":
                            running = false;
                            break;
                        default:
                            System.out.println("WARNING: Enter a menu number from 0 to 7.");
                    }
                } catch (IllegalArgumentException exception) {
                    // Part 6: invalid input returns to the menu without changing valid bookings.
                    System.out.println("WARNING: " + exception.getMessage());
                } catch (NoSuchElementException exception) {
                    // End-of-input is a normal exit, including a closed terminal input stream.
                    System.out.println("Input ended. Closing the session.");
                    running = false;
                }
            }
        }
        display("Final bookings", manager.getAll());
        System.out.println("Goodbye. Session ended; bookings are not saved.");
    }

    /** Prints the available actions before asking for a menu choice. */
    private static void printMenu() {
        System.out.println("\n========== APPOINTMENT MENU ==========");
        System.out.println("1. View medical staff");
        System.out.println("2. Add an appointment");
        System.out.println("3. View all appointments (sorted by time)");
        System.out.println("4. Find appointments by staff ID");
        System.out.println("5. Find appointments by patient phone");
        System.out.println("6. Cancel by staff ID and time");
        System.out.println("7. Cancel all appointments by patient phone");
        System.out.println("0. Exit");
    }

    /** Displays role-specific information and availability on the service date. */
    private static void displayStaff(MedicalProfessional[] staff, AppointmentManager manager) {
        System.out.println("\nMedical staff for " + manager.getServiceDate());
        for (MedicalProfessional professional : staff) {
            System.out.println(professional);
            System.out.println("Available today: "
                    + professional.worksOn(manager.getServiceDate().getDayOfWeek()));
        }
    }

    /** Reads one line, preserving leading zeroes in IDs and phone numbers. */
    private static String read(Scanner input, String prompt) {
        System.out.print(prompt);
        System.out.flush();
        return input.nextLine().trim();
    }

    /** Parses a strict HH:mm time; the appointment rules validate the allowed slot. */
    private static LocalTime readTime(Scanner input) {
        String value = read(input, "Start time (HH:mm, e.g. 09:00): ");
        if (!value.matches("[0-9]{2}:[0-9]{2}")) {
            throw new IllegalArgumentException("Use HH:mm, for example 09:00 or 15:30.");
        }
        try {
            return LocalTime.of(Integer.parseInt(value.substring(0, 2)),
                    Integer.parseInt(value.substring(3, 5)));
        } catch (DateTimeException exception) {
            throw new IllegalArgumentException("Enter a real clock time in HH:mm format.");
        }
    }

    /** Looks up a staff member by normalized identity, including leading-zero IDs. */
    private static MedicalProfessional findStaff(MedicalProfessional[] staff, String id) {
        String normalized = MedicalProfessional.normalizeId(id);
        for (MedicalProfessional professional : staff) {
            if (professional.getId().equals(normalized)) { return professional; }
        }
        throw new IllegalArgumentException("Unknown staff ID. Choose option 1 to view staff.");
    }

    /** Collects input; business validation and duplicate checks remain in the model. */
    private static void addBooking(Scanner input, MedicalProfessional[] staff,
            AppointmentManager manager) {
        String name = read(input, "Patient name: ");
        String phone = read(input, "Patient phone (7-15 digits): ");
        Patient patient = new Patient(name, phone);
        MedicalProfessional professional = findStaff(staff, read(input, "Staff ID (e.g. 101): "));
        LocalTime time = readTime(input);
        Appointment booking = new Appointment(patient, manager.getServiceDate(), time, professional);
        manager.add(booking);
    }

    /** Prints the supplied result without modifying the stored appointment list. */
    private static void display(String title, Appointment[] appointments) {
        System.out.println("\n" + title + " (" + appointments.length + ")");
        if (appointments.length == 0) { System.out.println("No appointments found."); }
        for (Appointment appointment : appointments) { System.out.println(appointment); }
    }
}

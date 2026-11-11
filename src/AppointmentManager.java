import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

/** Manages one day's bookings with array storage and clear operation feedback. */
public final class AppointmentManager {
    private final LocalDate serviceDate;
    private Appointment[] appointments = new Appointment[8];
    private int size;

    /** Creates a manager for today's appointments. */
    public AppointmentManager() { this(LocalDate.now()); }

    /** Allows deterministic tests against a single fixed day. */
    AppointmentManager(LocalDate serviceDate) {
        if (serviceDate == null) { throw new IllegalArgumentException("Service date is required."); }
        this.serviceDate = serviceDate;
    }

    /** @return the one day managed by this instance */
    public LocalDate getServiceDate() { return serviceDate; }

    /**
     * Adds a valid booking unless the staff identifier and start time conflict.
     * @param appointment booking to add
     * @return whether the booking was added
     */
    public boolean add(Appointment appointment) {
        if (appointment == null) { return failure("Appointment is required."); }
        if (!serviceDate.equals(appointment.getDate())) {
            return failure("Only appointments for " + serviceDate + " are accepted.");
        }
        for (int i = 0; i < size; i++) {
            // Staff identity is numeric ID, never a Java reference comparison.
            if (appointments[i].getProfessional().equals(appointment.getProfessional())
                    && appointments[i].getTime().equals(appointment.getTime())) {
                return failure("Duplicate booking for staff "
                        + appointment.getProfessional().getId() + " at " + appointment.getTime());
            }
        }
        if (size == appointments.length) {
            appointments = Arrays.copyOf(appointments, appointments.length * 2);
        }
        appointments[size++] = appointment;
        System.out.println("ADDED: " + appointment);
        return true;
    }

    /** @return all bookings in chronological order as an independent array */
    public Appointment[] getAll() { return sortedCopy(Arrays.copyOf(appointments, size)); }

    /** @param staffId digit-only professional ID
     * @return chronological matching bookings
     * @throws IllegalArgumentException if the ID is invalid */
    public Appointment[] findByStaffId(String staffId) {
        return filter(MedicalProfessional.normalizeId(staffId), true);
    }

    /** @param phone patient's phone
     * @return chronological matching bookings
     * @throws IllegalArgumentException if the phone is invalid */
    public Appointment[] findByPatientPhone(String phone) {
        return filter(Appointment.validatePhone(phone), false);
    }

    /**
     * Cancels the unique booking for a staff member and start time.
     * @param staffId numeric professional ID
     * @param time valid appointment start
     * @return whether a booking was removed
     */
    public boolean cancelByStaffAndTime(String staffId, LocalTime time) {
        try {
            String normalized = MedicalProfessional.normalizeId(staffId);
            Appointment.validateTime(time);
            for (int i = 0; i < size; i++) {
                if (appointments[i].getProfessional().getId().equals(normalized)
                        && appointments[i].getTime().equals(time)) {
                    removeAt(i);
                    System.out.println("CANCELLED: staff " + normalized + " at " + time);
                    return true;
                }
            }
            return failure("No booking for staff " + normalized + " at " + time);
        } catch (IllegalArgumentException exception) {
            return failure(exception.getMessage());
        }
    }

    /** @param phone patient phone whose bookings should all be cancelled
     * @return number removed, or zero if none matched or the phone was invalid */
    public int cancelByPatientPhone(String phone) {
        try { Appointment.validatePhone(phone); }
        catch (IllegalArgumentException exception) { failure(exception.getMessage()); return 0; }
        int removed = 0;
        // Walk backwards so shifting the array cannot skip adjacent matches.
        for (int i = size - 1; i >= 0; i--) {
            if (appointments[i].getPatientPhone().equals(phone)) { removeAt(i); removed++; }
        }
        if (removed == 0) { failure("No appointments for patient phone " + phone); }
        else { System.out.println("CANCELLED: " + removed + " appointment(s) for " + phone); }
        return removed;
    }

    private Appointment[] filter(String value, boolean byStaff) {
        Appointment[] matches = new Appointment[size];
        int count = 0;
        for (int i = 0; i < size; i++) {
            String candidate = byStaff ? appointments[i].getProfessional().getId()
                    : appointments[i].getPatientPhone();
            if (candidate.equals(value)) { matches[count++] = appointments[i]; }
        }
        return sortedCopy(Arrays.copyOf(matches, count));
    }

    private static Appointment[] sortedCopy(Appointment[] source) {
        Appointment[] result = source.clone();
        // Insertion sort keeps the introductory implementation explicit and readable.
        for (int i = 1; i < result.length; i++) {
            Appointment current = result[i];
            int j = i - 1;
            while (j >= 0 && result[j].compareTo(current) > 0) {
                result[j + 1] = result[j];
                j--;
            }
            result[j + 1] = current;
        }
        return result;
    }

    private void removeAt(int index) {
        for (int i = index; i < size - 1; i++) { appointments[i] = appointments[i + 1]; }
        appointments[--size] = null;
    }

    private static boolean failure(String message) {
        System.out.println("WARNING: " + message);
        return false;
    }
}

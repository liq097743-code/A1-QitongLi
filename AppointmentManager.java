import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

/** Manages one day's bookings with array storage and clear operation feedback. */
public final class AppointmentManager {
    private final LocalDate serviceDate;
    private Appointment[] appointments = new Appointment[8];
    private int size;

    /** Creates a manager for today's appointments. */
    public AppointmentManager() { this.serviceDate = LocalDate.now(); }

    /** @return the one day managed by this instance */
    public LocalDate getServiceDate() { return serviceDate; }

    /**
     * Adds a valid booking unless the staff identifier and start time conflict.
     * @param appointment booking to add
     * @return whether the booking was added
     */
    public boolean add(Appointment appointment) {
        if (appointment == null) { return failure("Appointment is required."); }
        if (!serviceDate.equals(LocalDate.now()) || !serviceDate.equals(appointment.getDate())) {
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
     * @return chronological matches, or an empty array after an invalid-input warning */
    public Appointment[] findByStaffId(String staffId) {
        try { return filter(MedicalProfessional.normalizeId(staffId), true); }
        catch (IllegalArgumentException exception) {
            failure(exception.getMessage());
            return new Appointment[0];
        }
    }

    /** @param phone patient's phone
     * @return chronological matches, or an empty array after an invalid-input warning */
    public Appointment[] findByPatientPhone(String phone) {
        try { return filter(Appointment.validatePhone(phone), false); }
        catch (IllegalArgumentException exception) {
            failure(exception.getMessage());
            return new Appointment[0];
        }
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
        // Sort an independent list so queries never rearrange internal storage.
        ArrayList<Appointment> result = new ArrayList<>(Arrays.asList(source));
        Collections.sort(result);
        return result.toArray(new Appointment[0]);
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

import java.time.LocalDate;
import java.time.LocalTime;

/** Immutable, comparable booking for one patient and one professional. */
public final class Appointment implements Comparable<Appointment> {
    private final String patientName;
    private final String patientPhone;
    private final LocalDate date;
    private final LocalTime time;
    private final MedicalProfessional professional;

    /**
     * Creates a booking; the manager additionally checks its service date.
     * @param patientName required patient name
     * @param patientPhone digit-only phone, 7 to 15 digits
     * @param date appointment date
     * @param time half-hour start from 08:00 through 15:30
     * @param professional selected staff member of any clinical type
     * @throws IllegalArgumentException if booking information is invalid
     */
    public Appointment(String patientName, String patientPhone, LocalDate date,
            LocalTime time, MedicalProfessional professional) {
        this.patientName = MedicalProfessional.requireText(patientName, "Patient name");
        this.patientPhone = validatePhone(patientPhone);
        if (date == null || professional == null) {
            throw new IllegalArgumentException("Date and medical professional are required.");
        }
        validateTime(time);
        if (!professional.worksOn(date.getDayOfWeek())) {
            throw new IllegalArgumentException("Selected professional does not work on "
                    + date.getDayOfWeek() + ".");
        }
        this.date = date;
        this.time = time;
        this.professional = professional;
    }

    /** @return patient name */
    public String getPatientName() { return patientName; }
    /** @return phone with leading zeroes preserved */
    public String getPatientPhone() { return patientPhone; }
    /** @return booking date */
    public LocalDate getDate() { return date; }
    /** @return chronological booking time */
    public LocalTime getTime() { return time; }
    /** @return immutable professional */
    public MedicalProfessional getProfessional() { return professional; }

    /** @param other booking to compare
     * @return date/time order, with staff identifier as a deterministic tie-break */
    @Override
    public int compareTo(Appointment other) {
        int result = date.compareTo(other.date);
        if (result == 0) { result = time.compareTo(other.time); }
        if (result == 0) { result = professional.getId().compareTo(other.professional.getId()); }
        return result;
    }

    /** @return readable booking details including the professional's full details */
    @Override
    public String toString() {
        return date + " " + time + " | Patient: " + patientName + " | Phone: "
                + patientPhone + " | " + professional;
    }

    /** Rejects invalid intervals and sub-minute times rather than rounding them. */
    static void validateTime(LocalTime time) {
        if (time == null || time.isBefore(LocalTime.of(8, 0))
                || !time.isBefore(LocalTime.of(16, 0))
                || time.getMinute() % 30 != 0 || time.getSecond() != 0
                || time.getNano() != 0) {
            throw new IllegalArgumentException("Use a 30-minute slot from 08:00 to 15:30; closes at 16:00.");
        }
    }

    /** Centralises phone validation so filtering and cancellation follow booking rules. */
    static String validatePhone(String phone) {
        if (phone == null || !phone.matches("[0-9]{7,15}")) {
            throw new IllegalArgumentException("Patient phone must contain 7 to 15 digits.");
        }
        return phone;
    }
}

import java.time.LocalDate;
import java.time.LocalTime;

/** Immutable, comparable booking for one patient and one professional. */
public final class Appointment implements Comparable<Appointment> {
    private final Patient patient;
    private final LocalDate date;
    private final int timeSlotMinutes;
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
        this(new Patient(patientName, patientPhone), date, time, professional);
    }

    /**
     * Creates a booking from a defensive snapshot of patient contact details.
     * @param patient required patient
     * @param date booking date
     * @param time valid slot start
     * @param professional chosen professional
     * @throws IllegalArgumentException if required information is invalid
     */
    public Appointment(Patient patient, LocalDate date, LocalTime time,
            MedicalProfessional professional) {
        if (patient == null) { throw new IllegalArgumentException("Patient is required."); }
        this.patient = new Patient(patient.getName(), patient.getMobilePhone());
        if (date == null || professional == null) {
            throw new IllegalArgumentException("Date and medical professional are required.");
        }
        validateTime(time);
        if (!professional.worksOn(date.getDayOfWeek())) {
            throw new IllegalArgumentException("Selected professional does not work on "
                    + date.getDayOfWeek() + ".");
        }
        this.date = date;
        this.timeSlotMinutes = time.getHour() * 60 + time.getMinute();
        this.professional = professional;
    }

    /** @return patient name */
    public String getPatientName() { return patient.getName(); }
    /** @return phone with leading zeroes preserved */
    public String getPatientPhone() { return patient.getMobilePhone(); }
    /** @return independent patient details, protecting stored bookings */
    public Patient getPatient() { return new Patient(patient.getName(), patient.getMobilePhone()); }
    /** @return numeric minutes since midnight used for ordering */
    public int getTimeSlotMinutes() { return timeSlotMinutes; }
    /** @return booking date */
    public LocalDate getDate() { return date; }
    /** @return chronological booking time */
    public LocalTime getTime() { return LocalTime.of(timeSlotMinutes / 60, timeSlotMinutes % 60); }
    /** @return immutable professional */
    public MedicalProfessional getProfessional() { return professional; }

    /** @param other booking to compare
     * @return date/time order, with staff identifier as a deterministic tie-break */
    @Override
    public int compareTo(Appointment other) {
        int result = date.compareTo(other.date);
        if (result == 0) { result = Integer.compare(timeSlotMinutes, other.timeSlotMinutes); }
        if (result == 0) { result = professional.getId().compareTo(other.professional.getId()); }
        return result;
    }

    /** @return readable booking details including the professional's full details */
    @Override
    public String toString() {
        return date + " " + ClinicSchedule.formatTime(timeSlotMinutes) + " | " + patient + " | " + professional;
    }

    /** Rejects invalid intervals and sub-minute times rather than rounding them. */
    static void validateTime(LocalTime time) {
        if (time == null || !ClinicSchedule.isValidSlot(time.getHour() * 60 + time.getMinute())
                || time.getSecond() != 0
                || time.getNano() != 0) {
            throw new IllegalArgumentException("Use a " + ClinicSchedule.SLOT_MINUTES
                    + "-minute slot from " + ClinicSchedule.formatTime(ClinicSchedule.OPENING_MINUTES)
                    + "; closing time is " + ClinicSchedule.formatTime(ClinicSchedule.CLOSING_MINUTES) + ".");
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

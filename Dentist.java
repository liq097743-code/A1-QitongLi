import java.time.DayOfWeek;

/** A dentist with role-specific clinical information. */
public final class Dentist extends MedicalProfessional {
    private final String dentalSpecialty;

    /**
     * Creates a dentist.
     * @param id numeric staff identifier
     * @param name full name
     * @param workingDays fixed weekly working days
     * @param dentalSpecialty dental specialty
     * @throws IllegalArgumentException if required information is invalid
     */
    public Dentist(String id, String name, DayOfWeek[] workingDays,
            String dentalSpecialty) {
        super(id, name, workingDays);
        this.dentalSpecialty = requireText(dentalSpecialty, "Dental specialty");
    }

    /** @return dental specialty */
    public String getDentalSpecialty() { return dentalSpecialty; }

    /** @return the role and its distinct clinical details */
    @Override
    protected String roleDetails() {
        return "Dentist | Dental specialty: " + dentalSpecialty;
    }
}

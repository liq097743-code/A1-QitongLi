import java.time.DayOfWeek;

/** A general practitioner with role-specific clinical information. */
public final class GeneralPractitioner extends MedicalProfessional {
    private final String practiceFocus;

    /**
     * Creates a general practitioner.
     * @param id numeric staff identifier
     * @param name full name
     * @param workingDays fixed weekly working days
     * @param practiceFocus practice focus
     * @throws IllegalArgumentException if required information is invalid
     */
    public GeneralPractitioner(String id, String name, DayOfWeek[] workingDays,
            String practiceFocus) {
        super(id, name, workingDays);
        this.practiceFocus = requireText(practiceFocus, "Practice focus");
    }

    /** @return practice focus */
    public String getPracticeFocus() { return practiceFocus; }

    /** @return the role and its distinct clinical details */
    @Override
    protected String roleDetails() {
        return "General practitioner | Practice focus: " + practiceFocus;
    }
}

import java.time.DayOfWeek;

/** A physiotherapist with role-specific clinical information. */
public final class Physiotherapist extends MedicalProfessional {
    private final String therapyApproach;

    /**
     * Creates a physiotherapist.
     * @param id numeric staff identifier
     * @param name full name
     * @param workingDays fixed weekly working days
     * @param therapyApproach therapy approach
     * @throws IllegalArgumentException if required information is invalid
     */
    public Physiotherapist(String id, String name, DayOfWeek[] workingDays,
            String therapyApproach) {
        super(id, name, workingDays);
        this.therapyApproach = requireText(therapyApproach, "Therapy approach");
    }

    /** @return therapy approach */
    public String getTherapyApproach() { return therapyApproach; }

    /** @return the role and its distinct clinical details */
    @Override
    protected String roleDetails() {
        return "Physiotherapist | Therapy approach: " + therapyApproach;
    }
}

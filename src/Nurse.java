import java.time.DayOfWeek;

/** A nurse with role-specific clinical information. */
public final class Nurse extends MedicalProfessional {
    private final String clinicalQualification;

    /**
     * Creates a nurse.
     * @param id numeric staff identifier
     * @param name full name
     * @param workingDays fixed weekly working days
     * @param clinicalQualification clinical qualification
     * @throws IllegalArgumentException if required information is invalid
     */
    public Nurse(String id, String name, DayOfWeek[] workingDays,
            String clinicalQualification) {
        super(id, name, workingDays);
        this.clinicalQualification = requireText(clinicalQualification, "Clinical qualification");
    }

    /** @return clinical qualification */
    public String getClinicalQualification() { return clinicalQualification; }

    /** @return the role and its distinct clinical details */
    @Override
    protected String roleDetails() {
        return "Nurse | Clinical qualification: " + clinicalQualification;
    }
}

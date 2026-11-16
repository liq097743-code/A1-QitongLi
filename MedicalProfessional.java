import java.time.DayOfWeek;
import java.util.Arrays;

/** Immutable common identity and weekly availability for clinical staff. */
public abstract class MedicalProfessional {
    private final String id;
    private final String name;
    private final DayOfWeek[] workingDays;

    /**
     * Creates valid clinical staff; duplicate weekdays are rejected.
     * @param id digit-only staff identifier
     * @param name full name
     * @param workingDays nonempty fixed weekly schedule
     * @throws IllegalArgumentException if any required value is invalid
     */
    protected MedicalProfessional(String id, String name, DayOfWeek[] workingDays) {
        this.id = normalizeId(id);
        this.name = requireText(name, "Staff name");
        if (workingDays == null || workingDays.length == 0) {
            throw new IllegalArgumentException("At least one working day is required.");
        }
        this.workingDays = workingDays.clone();
        for (int i = 0; i < this.workingDays.length; i++) {
            if (this.workingDays[i] == null) {
                throw new IllegalArgumentException("Working days cannot contain null.");
            }
            for (int j = 0; j < i; j++) {
                if (this.workingDays[i] == this.workingDays[j]) {
                    throw new IllegalArgumentException("Duplicate working day.");
                }
            }
        }
        Arrays.sort(this.workingDays);
    }

    /** @return canonical numeric staff identifier */
    public final String getId() { return id; }

    /** @return staff member's name */
    public final String getName() { return name; }

    /** @return an independent copy of the fixed weekly schedule */
    public final DayOfWeek[] getWorkingDays() { return workingDays.clone(); }

    /** @param day weekday to check
     * @return whether this professional works on that weekday */
    public final boolean worksOn(DayOfWeek day) {
        for (DayOfWeek workingDay : workingDays) {
            if (workingDay == day) { return true; }
        }
        return false;
    }

    /** @return role and role-specific clinical information */
    protected abstract String roleDetails();

    /** @return readable common and role-specific information */
    @Override
    public final String toString() {
        return "Staff " + id + " | " + name + " | Days: "
                + Arrays.toString(workingDays) + " | " + roleDetails();
    }

    /** @param other object to compare
     * @return true for the same staff identifier, regardless of object identity */
    @Override
    public final boolean equals(Object other) {
        return other instanceof MedicalProfessional
                && id.equals(((MedicalProfessional) other).id);
    }

    /** @return hash based on the same identifier used by equals */
    @Override
    public final int hashCode() { return id.hashCode(); }

    /** Validates numeric identity without imposing an integer size limit. */
    static String normalizeId(String id) {
        if (id == null || !id.matches("[0-9]+")) {
            throw new IllegalArgumentException("Staff ID must contain digits only.");
        }
        return id.replaceFirst("^0+(?!$)", "");
    }

    /** Validates mandatory text shared by clinical roles and appointments. */
    static String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.trim();
    }
}

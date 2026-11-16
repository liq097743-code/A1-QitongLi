/** Patient contact details with validation on construction and updates. */
public final class Patient {
    private String name;
    private String mobilePhone;

    /** @param name patient name
     * @param mobilePhone digit-only phone number */
    public Patient(String name, String mobilePhone) {
        setName(name);
        setMobilePhone(mobilePhone);
    }

    /** @return patient name */
    public String getName() { return name; }

    /** @return phone number, preserving leading zeroes */
    public String getMobilePhone() { return mobilePhone; }

    /** @param name nonblank new name
     * @throws IllegalArgumentException if the name is missing */
    public void setName(String name) {
        this.name = MedicalProfessional.requireText(name, "Patient name");
    }

    /** @param mobilePhone new phone with 7 to 15 digits
     * @throws IllegalArgumentException if the phone is invalid */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = Appointment.validatePhone(mobilePhone);
    }

    /** @return readable patient contact details */
    @Override
    public String toString() { return "Patient: " + name + " | Phone: " + mobilePhone; }
}

/** Central opening, closing and appointment interval configuration, in minutes. */
public final class ClinicSchedule {
    /** Inclusive opening time: 08:00. */
    public static final int OPENING_MINUTES = 8 * 60;
    /** Exclusive closing time: 16:00. */
    public static final int CLOSING_MINUTES = 16 * 60;
    /** Duration of one appointment slot. */
    public static final int SLOT_MINUTES = 30;

    private ClinicSchedule() { }

    /** @param minutes minutes since midnight
     * @return whether a complete slot fits within opening hours */
    public static boolean isValidSlot(int minutes) {
        return minutes >= OPENING_MINUTES && minutes + SLOT_MINUTES <= CLOSING_MINUTES
                && (minutes - OPENING_MINUTES) % SLOT_MINUTES == 0;
    }

    /** @param minutes minutes since midnight
     * @return formatted time in HH:mm */
    public static String formatTime(int minutes) {
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
    }
}

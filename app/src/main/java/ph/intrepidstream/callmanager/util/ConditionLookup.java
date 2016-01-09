package ph.intrepidstream.callmanager.util;

public enum ConditionLookup {

    STARTS_WITH("starts with"), NOT_STARTS_WITH("not starts with"), EQUALS("equals"), NOT_EQUALS("not equals");

    private String displayText;

    ConditionLookup(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public static ConditionLookup findByDisplayText(String displayText) {
        for (ConditionLookup conditionLookup : values()) {
            if (displayText.equals(conditionLookup.displayText)) {
                return conditionLookup;
            }
        }
        throw new IllegalArgumentException("No value for the given argument.");
    }
}

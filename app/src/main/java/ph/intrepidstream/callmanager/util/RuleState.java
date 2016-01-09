package ph.intrepidstream.callmanager.util;

public enum RuleState {

    OFF("off"), WARN("warn"), BLOCK("block");

    private String displayText;

    RuleState(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public static RuleState findByDisplayText(String displayText) {
        for (RuleState ruleState : values()) {
            if (displayText.equals(ruleState.displayText)) {
                return ruleState;
            }
        }
        throw new IllegalArgumentException("No value for the given argument.");
    }
}

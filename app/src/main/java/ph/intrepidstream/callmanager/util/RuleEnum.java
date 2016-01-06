package ph.intrepidstream.callmanager.util;

public enum RuleEnum {

    STARTS_WITH("starts with"), NOT_STARTS_WITH("not starts with"), EQUALS("equals"), NOT_EQUALS("not equals");

    private String displayString;

    RuleEnum(String displayString) {
        this.displayString = displayString;
    }

    public String getDisplayString() {
        return displayString;
    }
}

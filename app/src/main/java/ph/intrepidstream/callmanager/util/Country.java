package ph.intrepidstream.callmanager.util;

public enum Country {

    NONE("N/A", "N/A"),
    PHILIPPINES("Philippines", "+63"),
    INDONESIA("Indonesia", "+62"),
    THAILAND("Thailand", "+66"),
    MALAYSIA("Malaysia", "+60"),
    HONGKONG("Hong Kong", "+852"),
    SINGAPORE("Singapore", "+65"),
    VIETNAM("Vietnam", "+84"),
    MYANMAR("Myanmar", "+95"),
    CAMBODIA("Cambodia", "+855"),
    BRUNEI("Brunei", "+673"),
    MACAO("Macao", "+853"),
    LAOS("Laos", "+856"),
    INDIA("India", "+91"),
    CHINA("China", "+86"),
    JAPAN("Japan", "+81"),
    SOUTH_KOREA("South Korea", "+82"),
    TAIWAN("Taiwan", "+886"),
    MONGOLIA("Mongolia", "+976");

    private String displayName;
    private String countryCode;

    Country(String displayName, String countryCode) {
        this.displayName = displayName;
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

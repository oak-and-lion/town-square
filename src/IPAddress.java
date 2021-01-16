public class IPAddress {
    private String display;
    private String value;

    public IPAddress(String display, String value) {
        this.display = display;
        this.value = value;
    }

    public String getDisplay() {
        return display;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return getDisplay();
    }
}

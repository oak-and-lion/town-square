public class BooleanString {
    private Boolean bool;
    private String string;
    public BooleanString(Boolean bool, String string) {
        this.bool = bool;
        this.string = string;
    }
    public Boolean getBoolean() {
        return bool;
    }
    public String getString() {
        return string;
    }
    public void setBoolean(Boolean value) {
        bool = value;
    }
    public void setString(String value) {
        string = value;
    }
}

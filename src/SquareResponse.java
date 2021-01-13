public class SquareResponse {
    private String response;

    public String toString() {
        return response;
    }

    public SquareResponse() {
        response = "400:unknown request";
    }

    public SquareResponse(String value) {
        response = value;
    }

    public void setResponse(String value) {
        response = value;
    }
}

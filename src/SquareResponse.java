public class SquareResponse {
    private String response;
    private String[] responseSplit;

    private void setResponseValues() {
        responseSplit = response.split(":");
    }

    public String toString() {
        return response;
    }

    public SquareResponse() {
        setResponse("400:unknown request");
        
    }

    public SquareResponse(String value) {
        setResponse(value);
    }

    public void setResponse(String value) {
        response = value;
        setResponseValues();
    }

    public String getCode() {
        return responseSplit[0];
    }

    public String getMessage() {
        return responseSplit[1];
    }
}

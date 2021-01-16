public class SquareResponse {
    private String response;
    private String[] responseSplit;

    private void setResponseValues() {
        responseSplit = response.split(Constants.COLON);
    }

    public String toString() {
        return response;
    }

    public SquareResponse() {
        setResponse(Constants.UNKNOWN_REQUEST + Constants.COLON + Constants.UNKNOWN_REQUEST_MESSAGE);

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

    public String[] getResponseSplit() {
        return responseSplit;
    }
}

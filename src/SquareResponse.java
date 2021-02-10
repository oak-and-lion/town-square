public class SquareResponse {
    private String response;
    private String[] responseSplit;

    private void setResponseValues() {
        responseSplit = response.split(Constants.COLON);
        if (responseSplit.length < 1) {
            responseSplit = new String[2];
            responseSplit[0] = Constants.EMPTY_STRING;
            responseSplit[1] = Constants.EMPTY_STRING;
        } else if (responseSplit.length < 2) {
            String temp = responseSplit[0];
            responseSplit = new String[2];
            responseSplit[0] = temp;
            responseSplit[1] = Constants.EMPTY_STRING;
        }
    }

    public String toString() {
        return response;
    }

    public SquareResponse() {
        setResponse(concatStrings(Constants.UNKNOWN_REQUEST, Constants.COLON, Constants.UNKNOWN_REQUEST_MESSAGE));
    }

    public SquareResponse(String value) {
        setResponse(value);
    }

    public SquareResponse(String responseCode, String responseMessage) {
        setResponse(concatStrings(responseCode, Constants.COLON, responseMessage));
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

    public String concatStrings(String... strings) {
        StringBuilder result = new StringBuilder();

        for (String string : strings) {
            result.append(string);
        }

        return result.toString();
    }
}

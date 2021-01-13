public class FileWriteResponse {
    private boolean success;
    private int lines;
    public FileWriteResponse(boolean result, int lineCount) {
        success = result;
        lines = lineCount;
    }

    public void setSuccessful(boolean value) {
        success = value;
    }

    public boolean isSuccessful() {
        return success;
    }

    public void setLineCount(int value) {
        lines = value;
    }

    public int getLineCount() {
        return lines;
    }
}

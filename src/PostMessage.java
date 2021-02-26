import java.util.Arrays;

public class PostMessage implements Comparable<PostMessage> {
    private long millis;
    private String message;
    private IUtility utility;

    public PostMessage(long millis, String message, IUtility utility) {
        this.millis = millis;
        this.message = message;
        this.utility = utility;
    }

    public long getMillis() {
        return millis;
    }

    public String getMessage() {
        return message;
    }

    public void appendToMessage(String append) {
        message += append;
    }

    @Override
    public int compareTo(PostMessage m) {
        if (m.getMillis() == getMillis()) {
            return 0;
        }

        if (m.getMillis() > this.getMillis()) {
            return -1;
        }

        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof PostMessage) {
            PostMessage m = null;
            try {
                m = (PostMessage)o;
            } catch (Exception e) {
                utility.logError(utility.concatStrings(e.getMessage(), Constants.NEWLINE, Arrays.toString(e.getStackTrace())));
                return false;
            }

            return compareTo(m) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }
}

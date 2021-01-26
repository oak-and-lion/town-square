import java.util.ArrayList;
import java.util.Collections;

public class PostMessageList {

    private ArrayList<PostMessage> internalList = new ArrayList<PostMessage>();

    public void addAll(PostMessage[] messages) {
        for(PostMessage m : messages) {
            add(m);
        }
    }

    public void add(PostMessage e) {
        boolean found = false;
        for (PostMessage i : internalList) {
            if (i.getMillis() == e.getMillis()) {
                found = true;
                break;
            }
        }
        if (!found) {
            internalList.add(e);
            Collections.sort(internalList, null);
        }
    }

    public PostMessage get(int i) {
        return internalList.get(i);
    }

    public int size() {
        return internalList.size();
    }

    public PostMessage[] getAll() {
        return internalList.toArray(new PostMessage[internalList.size()]);
    }

    public String[] getAllMessages() {
        ArrayList<String> result = new ArrayList<String>();
        for(PostMessage msg : internalList) {
            result.add(msg.getMessage());
        }

        return result.toArray(new String[result.size()]);
    }
}

import java.util.ArrayList;

public class AliasObject {
    private String uniqueId;
    private String publicKey;
    private ArrayList<String> ips;
    private ArrayList<String> ports;
    
    public AliasObject(String aliasList, String publicKey) {
        String[] data = aliasList.split(Constants.QUESTION_MARK_SPLIT);
        this.uniqueId = data[0];
        this.ips = new ArrayList<>();
        this.ports = new ArrayList<>();

        String[] temp = data[1].split(Constants.FORWARD_SLASH);

        for (String t : temp) {
            String[] address = t.split(Constants.COLON);
            ips.add(address[0]);
            ports.add(address[1]);
        } 
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public DoubleString getAlias(int index) {
        return new DoubleString(ips.get(index), ports.get(index));
    }

    public String getIp(int index) {
        return ips.get(index);
    }

    public String getPort(int index) {
        return ports.get(index);
    }

    public int length() {
        return ips.size();
    }
}

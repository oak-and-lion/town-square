import java.util.Arrays;

public class MemberInfo implements Comparable<MemberInfo> {
    private String name;
    private String publicKey;
    private String ip;
    private String port;
    private String uniqueId;
    private IUtility utility;
    private Boolean online;

    public MemberInfo(String info, IUtility utility) {
        String[] split = info.split(Constants.FILE_DATA_SEPARATOR);

        name = split[0];
        publicKey = split[1];
        ip = split[2];
        port = split[3];
        uniqueId = split[4];
        this.utility = utility;
        online = false;
    }

    public MemberInfo(String name, String publicKey, String ip, String port, String uniqueId, IUtility utility) {
        this.name = name;
        this.publicKey = publicKey;
        this.ip = ip;
        this.port = port;
        this.uniqueId = uniqueId;
        this.utility = utility;
    }

    public String getName() {
        return name;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public Boolean isOnline() {
        return online;
    }

    public void setOnline(Boolean value) {
        online = value;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getName());
        result.append(Constants.FILE_DATA_SEPARATOR);
        result.append(getPublicKey());
        result.append(Constants.FILE_DATA_SEPARATOR);
        result.append(getIp());
        result.append(Constants.FILE_DATA_SEPARATOR);
        result.append(getPort());
        result.append(Constants.FILE_DATA_SEPARATOR);
        result.append(getUniqueId());
        return result.toString();
    }

    @Override
    public int compareTo(MemberInfo m) {
        int result = getUniqueId().compareTo(m.getUniqueId());

        if (result == 0) {
            result = getIp().compareTo(m.getIp());

            if (result == 0) {
                result = getPort().compareTo(m.getPort());
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof MemberInfo) {
            MemberInfo m = null;
            try {
                m = (MemberInfo)o;
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
        result = prime * result + ((uniqueId == null) ? 0 : uniqueId.hashCode());
        return result;
    }
}

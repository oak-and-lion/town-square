import java.util.ArrayList;
import java.util.Collections;

public class MemberInfoList {
    private ArrayList<MemberInfo> members = new ArrayList<>();
    private ArrayList<String> uniqueIds = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();

    public void addAll(MemberInfo[] memberInfos) {
        for (MemberInfo memberInfo : memberInfos) {
            add(memberInfo);
        }
    }

    public void add(MemberInfo memberInfo) {
        boolean found = false;

        for (MemberInfo member : members) {
            if (memberInfo.getUniqueId().equals(member.getUniqueId()) && memberInfo.getIp().equals(member.getIp())
                    && memberInfo.getPort().equals(member.getPort())) {
                found = true;
                break;
            }
        }

        if (!found) {
            members.add(memberInfo);
            uniqueIds.add(memberInfo.getUniqueId());
            names.add(memberInfo.getName());
            Collections.sort(members, null);
        }
    }

    public boolean containsName(String name, String uniqueId) {
        boolean sameUniqueId = uniqueIds.contains(uniqueId);

        if (sameUniqueId) return false;

        return names.contains(name);
    }

    public boolean containsUniqueId(String uniqueId) {
        return uniqueIds.contains(uniqueId);
    }

    public String[] getUniqueIds() {
        return uniqueIds.toArray(new String[uniqueIds.size()]);
    }

    public boolean contains(MemberInfo memberInfo) {
        return members.contains(memberInfo);
    }

    public MemberInfo get(int i) {
        return members.get(i);
    }

    public int size() {
        return members.size();
    }

    public MemberInfo[] getAll() {
        return members.toArray(new MemberInfo[members.size()]);
    }
}

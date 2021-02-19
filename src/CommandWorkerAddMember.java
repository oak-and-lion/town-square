import java.util.ArrayList;
import java.util.List;

public class CommandWorkerAddMember extends CommandWorkerBase implements ICommandWorker {
    public CommandWorkerAddMember(IUtility utility, ISquare square, IDialogController parent) {
        super(utility, square, parent);
    }

    public List<BooleanString> doWork(String commandArgs) {
        String[] info = commandArgs.split(Constants.FILE_DATA_SEPARATOR);
        String ip = info[1];
        String port = info[2];
        String file = utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT);
        String uniqueId = utility.readFile(Constants.UNIQUE_ID_FILE);
        ArrayList<BooleanString> result = new ArrayList<>();

        if (info[0].equals(Constants.SELF_COMMAND)) {
            MemberInfoList members = new MemberInfoList();
            String[] fileMembers = utility.readFile(file).split(Constants.READ_FILE_DATA_SEPARATOR);
            for (String fileMember : fileMembers) {
                members.add(new MemberInfo(fileMember, utility));
            }
            MemberInfo found = null;
            MemberInfo me = null;
            for (MemberInfo member : members.getAll()) {
                if (member.getUniqueId().equals(uniqueId)) {
                    me = member;
                }
                if (member.getUniqueId().equals(uniqueId) && member.getIp().equals(ip)
                        && member.getPort().equals(port)) {
                    found = member;
                }
            }

            if (found == null && me != null) {
                found = new MemberInfo(me.getName(), me.getPublicKey(), ip, port, uniqueId, utility);
                utility.appendToFile(utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT),
                        utility.concatStrings(Constants.NEWLINE, found.toString()));
            }
            result.add(new BooleanString(true, Constants.EMPTY_STRING));
        } else {
            result.add(new BooleanString(false, Constants.EMPTY_STRING));
        }

        return result;
    }
}

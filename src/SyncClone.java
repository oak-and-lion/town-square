import java.util.ArrayList;
import java.util.Arrays;

public class SyncClone extends Thread implements ISyncClone {
    private IUtility utility;
    private MemberInfoList memberInfoList;
    private ILogIt logger;
    private ArrayList<String> squareIds;

    public SyncClone(IUtility utility, IApp parent, ILogIt logger) {
        this.utility = utility;
        this.memberInfoList = new MemberInfoList();
        this.logger = logger;
        squareIds = new ArrayList<>();
    }

    @Override
    public void run() {
        String[] files;
        String uniqueId = utility.readFile(Constants.UNIQUE_ID_FILE);
        String ip = utility.readFile(Constants.IP_FILE);
        String port = utility.readFile(Constants.PORT_FILE);
        while (true) {
            files = utility.getFiles(Constants.MEMBERS_FILE_EXT);
            for (String file : files) {
                getMembers(file, uniqueId, ip, port);
            }

            files = utility.getFiles(Constants.SQUARE_FILE_EXT);
            squareIds.clear();
            for (String file: files ) {
                getKnownSquares(file);
            }

            for (MemberInfo member : memberInfoList.getAll()) {
                logger.logInfo(utility.concatStrings("Syncing clone: ", member.getName(), Constants.SPACE,
                        member.getIp(), Constants.SPACE, member.getPort()));
            }

            try {
                Thread.sleep(Constants.SYNC_CLONE_WAIT);
            } catch (InterruptedException ie) {
                utility.logError(
                        utility.concatStrings(ie.getMessage(), Constants.NEWLINE, Arrays.toString(ie.getStackTrace())));
                Thread.currentThread().interrupt();
            }
        }
    }

    private void getMembers(String file, String uniqueId, String ip, String port) {
        String[] members = utility.readFile(file).split(Constants.READ_FILE_DATA_SEPARATOR);
        for (String member : members) {
            MemberInfo mi = new MemberInfo(member, utility);
            if (mi.getUniqueId().equals(uniqueId)) {
                if (mi.getIp().equals(ip) && mi.getPort().equals(port)) {
                    continue;
                }
                this.memberInfoList.add(mi);
            }
        }
    }

    private void getKnownSquares(String file) {
        String squareInfo = utility.readFile(file);
        String[] si = squareInfo.split(Constants.COMMA);
        squareIds.add(si[1]);
    }
}
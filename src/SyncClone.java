import java.util.ArrayList;
import java.util.Arrays;

public class SyncClone extends Thread implements ISyncClone {
    private IUtility utility;
    private MemberInfoList memberInfoList;
    private ILogIt logger;
    private ArrayList<String> squareIds;
    private ArrayList<String> squareInfos;
    private IApp parent;

    public SyncClone(IUtility utility, IApp parent, ILogIt logger) {
        this.utility = utility;
        this.memberInfoList = new MemberInfoList();
        this.logger = logger;
        this.parent = parent;
        squareIds = new ArrayList<>();
        squareInfos = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            // not sure what to do here
            // basically, need to go to each member and tell them the squares we already have
            // then that member returns the squares that we don't

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

        squareInfos.add(squareInfo);
    }
}
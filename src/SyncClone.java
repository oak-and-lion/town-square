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
            squareInfos.clear();
            for (String file : files) {
                getKnownSquares(file);
            }

            ICommandController cmdController = parent.getDialogController().getCommandController();
            IFactory factory = parent.getDialogController().getFactory();

            for (MemberInfo member : memberInfoList.getAll()) {
                ISquare square = factory.createSquare(Constants.BASE_SQUARE, squareInfos.get(0), port, ip,
                        factory.createSquareController(Constants.BASE_SQUARE_CONTROLLER, utility,
                                parent.getDialogController(), logger,
                                factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility)),
                        utility, parent.getDialogController(), uniqueId, parent);
                cmdController.processCommand(utility.concatStrings(Constants.FORWARD_SLASH, Constants.NULL_TEXT),
                        square);
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

        squareInfos.add(squareInfo);
    }
}
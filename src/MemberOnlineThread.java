import java.util.Arrays;

public class MemberOnlineThread extends Thread implements IMemberOnlineThread {
    private MemberInfo member;
    private IUtility utility;
    private ISquare square;
    private ICommandController controller;
    private Boolean done;

    public MemberOnlineThread(MemberInfo member, IUtility utility, ISquare square) {
        this.member = member;
        this.utility = utility;
        this.square = square;
        this.controller = this.square.getSampleController().getCommandController();
        this.done = true;
    }

    @Override
    public void run() {
        done = false;
        try {
            String ack = utility.concatStrings(Constants.FORWARD_SLASH, Constants.ACK_COMMAND, Constants.SPACE,
                    member.getIp(), Constants.FILE_DATA_SEPARATOR, member.getPort());

            BooleanString[] result = controller.processCommand(ack, square);
            if (result != null && result.length > 0) {
                member.setOnline(result[0].getBoolean());
            }
        } catch (Exception e) {
            utility.logError(utility.concatStrings(e.getMessage(), Constants.NEWLINE, Arrays.toString(e.getStackTrace())));
        }
        done = true;
    }

    public Boolean isDone() {
        return done;
    }
}

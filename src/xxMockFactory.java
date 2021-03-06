import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

import javafx.scene.control.TextField;

public class xxMockFactory implements IFactory {
    public IMemberPostsThread createMemberPostsThread(int type, String info, String uniqueId, String[] msg,
            ISquare square, IUtility utility, IApp app) {
        return new xxMockMemberPostsThread();
    }

    public IUtility createUtility(int type, IDialogController controller) {
        return null;
    }

    public IAlertBox createAlertBox(int type) {
        return null;
    }

    public ISystemExit createSystemExit(int type) {
        return null;
    }

    public ILogIt createLogger(int type, String file, IUtility utility, IDialogController dialogController) {
        return null;
    }

    public IClientThread createClientThread(int type, ISquare square, IUtility utility, String uniqueId, IApp app) {
        return null;
    }

    public ICryptoUtils createCryptoUtils(int type, IDialogController controller) {
        return null;
    }

    public ISquareKeyPair createSquareKeyPair(int type, IUtility utility) {
        return null;
    }

    public ICommandController createCommandController(int type, IUtility utility, IDialogController controller) {
        return null;
    }

    public ISquareController createSquareController(int type, IUtility mainUtility, IDialogController controller,
            ILogIt logger, ISquareKeyPair keyPair) {
        return null;
    }

    public IServer createServer(int type, int port, ISquareController squareController, ILogIt logger, IApp app) {
        return null;
    }

    public ISquare createSquare(int type, String defaultSquareInfo, String port, String ip,
            ISquareController squareController, IUtility utility, IDialogController controller, String uniqueId, IApp app) {
        return null;
    }

    public IVersionChecker createVersionChecker(int type, IUtility utility, String uniqueId, IApp parent) {
        return null;
    }

    public IClient createClient(int type, String hostname, int port, String squareId, IApp parent) {
        return null;
    }

    public ISquareKeyPair createSquareKeyPair(int type, PublicKey publicKey, PrivateKey privateKey, IUtility utility) {
        return null;
    }

    public IServerThread createServerThread(int type, Socket socket, ISquareController squareController,
            ILogIt logger, IUtility utility, RequesterInfo requester) {
        return null;
    }

    public ITextDialogBox createTextDialogBox(int type, String title, String headerText, String content,
            ITextDialogBoxCallback controller, double width, int createType) {
        return null;
    }

    public ITownSquareButton createTownSquareButton(int type, String buttonText, ISquare square,
            TextField postsTextField) {
        return null;
    }

    public IModalViewer createModalViewer(int type) {
        return null;
    }

    public IModalViewer createModalViewer(int type, IUtility utility, ISquare square) {
        return null;
    }

    public ISquareWorker createSquareWorker(String command, IUtility utility, IDialogController dialogController, ILogIt logger) {
        return null;
    }

    public ISquare findSquareByCommand(String command, String inviteId, IDialogController dialogController) {
        return null;
    }

    public IShowSquareMembers createShowSquareMembers(int type) {
        return null;
    }

    public ICommandWorker createCommandWorker(String cmd, IUtility utility, ISquare square, IDialogController dialogController) {
        return null;
    }

    public IApp createApp(String loggerFlag, IAlertBox alertbox, ISystemExit exit, IFactory f) {
        return new AppBase(loggerFlag, alertbox, exit, f);
    }

    public IDialogController createDialogController(int type, IApp app, IUtility utility) {
        return new ServerDialogController(app, utility, this);
    }

    public ISyncClone createSyncClone(int type, IUtility utility, IApp parent, ILogIt logger) {
        return null;
    }

    public ISetup createSetup(int type, IUtility utility, IApp app) {
        return null;
    }
}

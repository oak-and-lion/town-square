import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

import javafx.scene.control.TextField;

public interface IFactory {
    IMemberPostsThread createMemberPostsThread(int type, String info, String uniqueId, String[] msg, ISquare square,
            IUtility utility, IApp app);
    IUtility createUtility(int type);
    IAlertBox createAlertBox(int type);
    ISystemExit createSystemExit(int type);
    ILogIt createLogger(int type, String file, IUtility utility, IDialogController dialogController);
    IClientThread createClientThread(int type, ISquare square, IUtility utility, String uniqueId, IApp app);
    ICryptoUtils createCryptoUtils(int type, IDialogController controller);
    ISquareKeyPair createSquareKeyPair(int type, IUtility utility);
    ICommandController createCommandController(int type, IUtility utility, IDialogController controller);
    ISquareController createSquareController(int type, IUtility mainUtility, IDialogController controller,
            ILogIt logger, ISquareKeyPair keyPair);
    IServer createServer(int type, int port, ISquareController squareController, ILogIt logger, IApp app);
    ISquare createSquare(int type, String defaultSquareInfo, String port, String ip,
        ISquareController squareController, IUtility utility, IDialogController controller, String uniqueId, IApp app);
    IVersionChecker createVersionChecker(int type, IUtility utility, String uniqueId, IApp parent);
    IClient createClient(int type, String hostname, int port, String squareId, IApp appParent);
    ISquareKeyPair createSquareKeyPair(int type);
    ISquareKeyPair createSquareKeyPair(int type, PublicKey publicKey, PrivateKey privateKey);
    IServerThread createServerThread(int type, Socket socket, ISquareController squareController,
            ILogIt logger, IUtility utility, RequesterInfo requester);
    ITextDialogBox createTextDialogBox(int type, String title, String headerText, String content,
            ITextDialogBoxCallback controller, double width, int createType);
    ITownSquareButton createTownSquareButton(int type, String buttonText, ISquare square,
            TextField postsTextField);
    IModalViewer createModalViewer(int type);
    IModalViewer createModalViewer(int type, IUtility utility, ISquare square);
    ISquareWorker createSquareWorker(String command, IUtility utility, IDialogController dialogController, ILogIt logger);
    ISquare findSquareByCommand(String command, String inviteId, IDialogController dialogController);
    IShowSquareMembers createShowSquareMembers(int type);
    ICommandWorker createCommandWorker(String cmd, IUtility utility, ISquare square, IDialogController dialogController);
    IApp createApp(String loggerFlag, IAlertBox alertbox, ISystemExit exit, IFactory f);
    IDialogController createDialogController(int type, IApp app, IUtility utility);
}

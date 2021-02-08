import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

import javafx.scene.control.TextField;

public class xxMockFactory implements IFactory {
    public IMemberPostsThread createMemberPostsThread(int type, String info, String uniqueId, String[] msg,
            ISquare square, IUtility utility) {
        return new xxMockMemberPostsThread();
    }

    public IUtility createUtility(int type) {
        return null;
    }

    public IAlertBox createAlertBox(int type) {
        return null;
    }

    public ISystemExit createSystemExit(int type) {
        return null;
    }

    public ILogIt createLogger(int type, String file, IUtility utility) {
        return null;
    }

    public IClientThread createClientThread(int type, ISquare square, IUtility utility, String uniqueId) {
        return null;
    }

    public ICryptoUtils createCryptoUtils(int type) {
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
            ISquareController squareController, IUtility utility, IDialogController controller, String uniqueId) {
        return null;
    }

    public IVersionChecker createVersionChecker(int type, IUtility utility, String uniqueId) {
        return null;
    }

    public IClient createClient(int type, String hostname, int port, String squareId) {
        return null;
    }

    public ISquareKeyPair createSquareKeyPair(int type) {
        return null;
    }

    public ISquareKeyPair createSquareKeyPair(int type, PublicKey publicKey, PrivateKey privateKey) {
        return null;
    }

    public IServerThread createServerThread(int type, Socket socket, ISquareController squareController,
            ILogIt logger, IUtility utility) {
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

    public IMemberAliasUpdateThread createMemberAliasUpdateThread(int type, String info, String uniqueId, ISquare square, IUtility utility) {
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
}

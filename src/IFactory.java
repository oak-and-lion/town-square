import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

import javafx.scene.control.TextField;

public interface IFactory {
    IMemberPostsThread createMemberPostsThread(int type, String info, String uniqueId, String[] msg, ISquare square,
            IUtility utility);
    IUtility createUtility(int type);
    IAlertBox createAlertBox(int type);
    ISystemExit createSystemExit(int type);
    ILogIt createLogger(int type, String file, IUtility utility);
    IClientThread createClientThread(int type, ISquare square, IUtility utility, String uniqueId);
    ICryptoUtils createCryptoUtils(int type);
    ISquareKeyPair createSquareKeyPair(int type, IUtility utility);
    ICommandController createCommandController(int type, IUtility utility, IDialogController controller);
    ISquareController createSquareController(int type, IUtility mainUtility, IDialogController controller,
            ILogIt logger, ISquareKeyPair keyPair);
    IServer createServer(int type, int port, ISquareController squareController, ILogIt logger);
    ISquare createSquare(int type, String defaultSquareInfo, String port, String ip,
        ISquareController squareController, IUtility utility, IDialogController controller, String uniqueId);
    IVersionChecker createVersionChecker(int type, IUtility utility, String uniqueId);
    IClient createClient(int type, String hostname, int port, String squareId);
    ISquareKeyPair createSquareKeyPair(int type);
    ISquareKeyPair createSquareKeyPair(int type, PublicKey publicKey, PrivateKey privateKey);
    IServerThread createServerThread(int type, Socket socket, ISquareController squareController,
            ILogIt logger);
    ITextDialogBox createTextDialogBox(int type, String title, String headerText, String content,
            ITextDialogBoxCallback controller, double width, int createType);
    ITownSquareButton createTownSquareButton(int type, String buttonText, ISquare square,
            TextField postsTextField);
    IModalViewer createModalViewer(int type);
    IMemberAliasUpdateThread createMemberAliasUpdateThread(int type, String info, String uniqueId, ISquare square, IUtility utility);
}

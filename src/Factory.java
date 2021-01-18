import java.net.Socket;

import javafx.scene.control.TextField;

public class Factory {
    private Factory() {
    }

    public static ISquareKeyPair createSquareKeyPair(int type) {
        if (type == Constants.BASE_SQUARE_KEY_PAIR) {
            return new SquareKeyPair();
        }

        return null;
    }

    public static ICryptoUtils createCryptoUtils(int type) {
        if (type == Constants.BASE_CRYPTO_UTILS) {
            return new CryptoUtils();
        }

        return null;
    }

    public static ISquareController createSquareController(int type, IUtility mainUtility,
            IDialogController controller, ILogIt logger) {
        if (type == Constants.BASE_SQUARE_CONTROLLER) {
            return new SquareController(mainUtility, controller, logger);
        }

        return null;
    }

    public static IServer createServer(int type, int port, ISquareController squareController, ILogIt logger) {
        if (type == Constants.BASE_SERVER) {
            return Server.create(port, squareController, logger);
        }

        return null;
    }

    public static IClient createClient(int type, String hostname, int port, String squareId) {
        if (type == Constants.BASE_CLIENT) {
            return new Client(hostname, port, squareId);
        }

        return null;
    }

    public static IUtility createUtility(int type) {
        if (type == Constants.BASE_UTILITY) {
            return Utility.create();
        }

        return null;
    }

    public static ISquare createSquare(int type, String defaultSquareInfo, String port, String ip,
            ISquareController squareController, IUtility utility, IDialogController controller, String uniqueId) {
        if (type == Constants.BASE_SQUARE) {
            return new Square(defaultSquareInfo, port, ip, squareController, utility, controller, uniqueId);
        }

        return null;
    }

    public static TownSquareButton createTownSquareButton(int type, String buttonText, ISquare square, TextField postsTextField) {
        if (type == Constants.BASE_TOWN_SQUARE_BUTTON) {
            return new TownSquareButton(buttonText, square, postsTextField);
        }

        return null;
    }

    public static ILogIt createLogger(int type, String file, IUtility utility) {
        if (type == Constants.CONSOLE_LOGGER) {
            return LogItConsole.create();
        } else if (type == Constants.FILE_LOGGER) {
            return LogItFile.create(utility, file);
        }

        return null;
    }

    public static IClientThread createClientThread(int type, Square square, IUtility utility, String uniqueId) {
        if (type == Constants.BASE_CLIENT_THREAD) {
            return new ClientThread(square, utility, uniqueId);
        }

        return null;
    }

    public static IServerThread createServerThread(int type, Socket socket, ISquareController squareController, ILogIt logger) {
        if (type == Constants.BASE_SERVER_THREAD) {
            return new ServerThread(socket, squareController, logger);
        }

        return null;
    }

    public static ITextDialogBox creaTextDialogBox(int type, String title, String headerText,
    String content, ITextDialogBoxCallback controller, double width, int createType) {
        if (type == Constants.BASE_TEXT_DIALOG_BOX) {
            return new TextDialogBox(title, headerText, content, controller, width, createType);
        }

        return null;
    }

    public static IAlertBox createAlertBox(int type) {
        if (type == Constants.BASE_ALERT_BOX) {
            return AlertBox.create();
        }

        return null;
    }

    public static ISystemExit createSystemExit(int type) {
        if (type == Constants.BASE_SYSTEM_EXIT) {
            return new SystemExit();
        }

        return null;
    }
}

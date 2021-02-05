import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

import javafx.scene.control.TextField;

public class Factory implements IFactory {
    public ISquareKeyPair createSquareKeyPair(int type, PublicKey publicKey, PrivateKey privateKey) {
        if (type == Constants.KEYS_SQUARE_KEY_PAIR) {
            return new SquareKeyPair(publicKey, privateKey);
        }

        return null;
    }

    public ISquareKeyPair createSquareKeyPair(int type) {
        if (type == Constants.BASE_SQUARE_KEY_PAIR) {
            return new SquareKeyPair();
        }

        return null;
    }

    public ISquareKeyPair createSquareKeyPair(int type, IUtility utility) {
        if (type == Constants.UTILITY_SQUARE_KEY_PAIR) {
            return new SquareKeyPair(utility);
        }

        return null;
    }

    public ICryptoUtils createCryptoUtils(int type) {
        if (type == Constants.BASE_CRYPTO_UTILS) {
            return new CryptoUtils(this);
        }

        return null;
    }

    public ISquareController createSquareController(int type, IUtility mainUtility, IDialogController controller,
            ILogIt logger, ISquareKeyPair keyPair) {
        if (type == Constants.BASE_SQUARE_CONTROLLER) {
            return new SquareController(mainUtility, controller, logger, keyPair, this);
        }

        return null;
    }

    public IServer createServer(int type, int port, ISquareController squareController, ILogIt logger) {
        if (type == Constants.BASE_SERVER) {
            return Server.create(port, squareController, logger, this);
        }

        return null;
    }

    public IClient createClient(int type, String hostname, int port, String squareId) {
        if (type == Constants.BASE_CLIENT) {
            return new Client(hostname, port, squareId, this);
        }

        return null;
    }

    public IUtility createUtility(int type) {
        if (type == Constants.BASE_UTILITY) {
            return Utility.create();
        }

        return null;
    }

    public ISquare createSquare(int type, String defaultSquareInfo, String port, String ip,
            ISquareController squareController, IUtility utility, IDialogController controller, String uniqueId) {
        if (type == Constants.BASE_SQUARE) {
            return new Square(defaultSquareInfo, port, ip, squareController, utility, controller, uniqueId, this);
        }

        return null;
    }

    public ITownSquareButton createTownSquareButton(int type, String buttonText, ISquare square,
            TextField postsTextField) {
        if (type == Constants.BASE_TOWN_SQUARE_BUTTON) {
            return new TownSquareButton(buttonText, square, postsTextField);
        }

        return null;
    }

    public ILogIt createLogger(int type, String file, IUtility utility) {
        if (type == Constants.CONSOLE_LOGGER) {
            return LogItConsole.create();
        } else if (type == Constants.FILE_LOGGER) {
            return LogItFile.create(utility, file);
        }

        return null;
    }

    public IClientThread createClientThread(int type, ISquare square, IUtility utility, String uniqueId) {
        if (type == Constants.BASE_CLIENT_THREAD) {
            return new ClientThread(square, utility, uniqueId, this);
        }

        return null;
    }

    public IServerThread createServerThread(int type, Socket socket, ISquareController squareController,
            ILogIt logger) {
        if (type == Constants.BASE_SERVER_THREAD) {
            return new ServerThread(socket, squareController, logger);
        }

        return null;
    }

    public ITextDialogBox createTextDialogBox(int type, String title, String headerText, String content,
            ITextDialogBoxCallback controller, double width, int createType) {
        if (type == Constants.BASE_TEXT_DIALOG_BOX) {
            return new TextDialogBox(title, headerText, content, controller, width, createType);
        }

        return null;
    }

    public IAlertBox createAlertBox(int type) {
        if (type == Constants.BASE_ALERT_BOX) {
            return AlertBox.create();
        }

        return null;
    }

    public ISystemExit createSystemExit(int type) {
        if (type == Constants.BASE_SYSTEM_EXIT) {
            return new SystemExit();
        }

        return null;
    }

    public IVersionChecker createVersionChecker(int type, IUtility utility, String uniqueId) {
        if (type == Constants.BASE_VERSION_CHECKER) {
            return new VersionChecker(utility, uniqueId, this);
        }

        return null;
    }

    public IModalViewer createModalViewer(int type) {
        if (type == Constants.BASE_MODAL_IMAGE_VIEWER) {
            return new ModalImageViewer();
        } else if (type == Constants.BASE_MODAL_VIDEO_VIEWER) {
            return new ModalVideoViewer();
        }

        return null;
    }

    public ICommandController createCommandController(int type, IUtility utility, IDialogController controller) {
        if (type == Constants.BASE_COMMAND_CONTROLLER) {
            return new CommandController(utility, controller);
        }

        return null;
    }

    public IMemberPostsThread createMemberPostsThread(int type, String info, String uniqueId, String[] msg,
            ISquare square, IUtility utility) {
        if (type == Constants.BASE_MEMBER_POSTS_THREAD) {
            return new MemberPostsThread(info, uniqueId, msg, square, utility, this);
        }

        return null;
    }

    public IMemberAliasUpdateThread createMemberAliasUpdateThread(int type, String info, String uniqueId, ISquare square, IUtility utility) {
        if (type == Constants.BASE_MEMBER_ALIAS_UPDATE_THREAD) {
            return new MemberAliasUpdateThread(this, info, uniqueId, square, utility);
        }

        return null;
    }
}

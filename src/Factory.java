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

    public IServer createServer(int type, int port, ISquareController squareController, ILogIt logger, IApp app) {
        if (type == Constants.BASE_SERVER) {
            return Server.create(port, squareController, logger, this, app);
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
            ILogIt logger, IUtility utility) {
        if (type == Constants.BASE_SERVER_THREAD) {
            return new ServerThread(socket, squareController, logger, utility);
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
        return createModalViewer(type, new xxMockIUtility(), new xxMockSquare());
    }

    public IModalViewer createModalViewer(int type, IUtility utility, ISquare square) {
        if (type == Constants.BASE_MODAL_IMAGE_VIEWER) {
            return new ModalImageViewer();
        } else if (type == Constants.BASE_MODAL_VIDEO_VIEWER) {
            return new ModalVideoViewer();
        } else if (type == Constants.BASE_MODAL_MEMBER_VIEWER) {
            return new ModalMembersList(utility, square);
        }

        return null;
    }

    public ICommandController createCommandController(int type, IUtility utility, IDialogController controller) {
        if (type == Constants.BASE_COMMAND_CONTROLLER) {
            return new CommandController(utility, controller, this);
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

    public ISquareWorker createSquareWorker(String command, IUtility utility, IDialogController dialogController, ILogIt logger) {
        if (command.equals(Constants.GET_APP_JAR_COMMAND)) {
            return new SquareWorkerAppJar(utility, command);
        } else if (command.equals(Constants.ACK_COMMAND)) {
            return new SquareWorkerAck(utility, command);
        } else if (command.equals(Constants.REQUEST_PUBLIC_KEY_COMMAND)) {
            return new SquareWorkerPublicKeyRequest(utility, command);
        } else if (command.equals(Constants.JOIN_COMMAND)) {
            return new SquareWorkerJoin(utility, dialogController, command);
        } else if (command.equals(Constants.READ_COMMAND)) {
            return new SquareWorkerRead(utility, this, logger, command);
        } else if (command.equals(Constants.FAILURE_COMMAND)) {
            return new SquareWorkerFailure(utility, command);
        } else if (command.equals(Constants.CLONE_COMMAND)) {
            return new SquareWorkerClone(utility, command);
        } else if (command.equals(Constants.REQUEST_FILE_COMMAND)) {
            return new SquareWorkerGetFile(utility, command, this);
        } else if (command.equals(Constants.READ_ALIAS_COMMAND)) {
            return new SquareWorkerReadAlias(utility, command, this);
        } else if (command.equals(Constants.CHECK_VERSION_COMMAND)) {
            return new SquareWorkerCheckVersion(utility, command);
        } 

        return new SquareWorkerEmpty(utility, command);
    }

    public ISquare findSquareByCommand(String command, String inviteId, IDialogController dialogController) {
        ISquare square;
        if (command.equals(Constants.CHECK_VERSION_COMMAND) || command.equals(Constants.GET_APP_JAR_COMMAND)) {
            square = new xxMockSquare();
        } else {
            square = dialogController.getSquareByInvite(inviteId);
        }

        return square;
    }

    public IShowSquareMembers createShowSquareMembers(int type) {
        if (type == Constants.BASE_SHOW_SQUARE_MEMBERS) {
            return new ShowSquareMembers();
        }

        return null;
    }

    public ICommandWorker createCommandWorker(String cmd, IUtility utility, ISquare square, IDialogController dialogController) {
        if (cmd.equals(Constants.NICKNAME_COMMAND)) {
            return new CommandWorkerNickName(utility, square, dialogController);
        } else if (cmd.equals(Constants.PAUSE_COMMAND)) {
            return new CommandWorkerPause(utility, square, dialogController);
        } else if (cmd.equals(Constants.BLOCK_COMMAND)) {
            return new CommandWorkerBlock(utility, square, dialogController);
        } else if (cmd.equals(Constants.ABOUT_COMMAND)) {
            return new CommandWorkerAbout(utility, square, dialogController);
        } else if (cmd.equals(Constants.UNPAUSE_COMMAND)) {
            return new CommandWorkerUnpause(utility, square, dialogController);
        } else if (cmd.equals(Constants.CLONE_COMMAND)) {
            return new CommandWorkerCreateClone(utility, square, dialogController, this);
        } else if (cmd.equals(Constants.DNA_COMMAND)) {
            return new CommandWorkerDNA(utility, square, dialogController);
        } else if (cmd.equals(Constants.SEND_CLONE_COMMAND)) {
            return new CommandWorkerGetClone(utility, square, dialogController, this);
        } else if (cmd.equals(Constants.LICENSE_COMMAND)) {
            return new CommandWorkerLicense(utility, square, dialogController);
        } else if (cmd.equals(Constants.EXPOSE_COMMAND)) {
            return new CommandWorkerExpose(utility, square, dialogController);
        } else if (cmd.equals(Constants.HIDE_COMMAND)) {
            return new CommandWorkerHide(utility, square, dialogController);
        } else if (cmd.equals(Constants.HELP_COMMAND)) {
            return new CommandWorkerHelp(utility, square, dialogController);
        } else if (cmd.equals(Constants.UNBLOCK_COMMAND)) {
            return new CommandWorkerUnblock(utility, square, dialogController);
        }

        return null;
    }
}

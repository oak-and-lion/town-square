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
            IDialogController controller) {
        if (type == Constants.BASE_SQUARE_CONTROLLER) {
            return new SquareController(mainUtility, controller);
        }

        return null;
    }

    public static IServer createServer(int type, int port, ISquareController squareController) {
        if (type == Constants.BASE_SERVER) {
            return Server.create(port, squareController);
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
}

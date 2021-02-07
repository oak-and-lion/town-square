public class ClientCmdTest {
    public static void main(String[] args) {
        // ========================================
        // setup
        //=========================================

        IFactory factory = new Factory();
        IUtility utility = factory.createUtility(1);

        //String hostname = "192.168.1.159";
        //int port = 44123;
        //String ip = "127.0.0.1";
        //String uniqueId = utility.readFile(Constants.UNIQUE_ID_FILE);
        //String defaultSquareInfo = utility.readFile(Constants.DEFAULT_SQUARE_FILE);
        
        ILogIt logger = factory.createLogger(1, "clientCmdTest.log", utility);
        
        String cmd = "clone%%%password";
        
        String text = "e%%%a7075b5b-b91d-4448-a0f9-d9b0bec1a726%%%";

        IDialogController dController = new xxMockIDialogController();
        dController.setCommandController(new CommandController(utility, dController, factory));

        ISquareKeyPair keyPair = new SquareKeyPair(utility);

        ISquareController squareController = factory.createSquareController(1, utility, dController, logger, keyPair);

        //ISquare square = factory.createSquare(1, defaultSquareInfo, Integer.toString(port), ip, squareController, utility, dController, uniqueId);
        
        //square.runClientFunctions(1);
        // ===============================================================

        //String info, String file, String uniqueId, String[] msg, ISquare square, IUtility utility

        SquareResponse keyResp = squareController.processRequest("u%%%a7075b5b-b91d-4448-a0f9-d9b0bec1a726%%%pkey");

        keyPair.setPublicKeyFromBase64(keyResp.getMessage());
        String key = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
        String e = utility.encrypt(cmd, key);
        String f = keyPair.encryptToBase64(key);
        String d = text + f + "%%%" + e;

        SquareResponse response = squareController.processRequest(d);

        String temp = "password________";

        String b64 = utility.decrypt(response.getMessage(), temp);

        byte[] data = utility.convertFromBase64(b64);

        utility.deleteFile("temp.zip");
        utility.writeBinaryFile("temp.zip", data);

        System.exit(0);
    }
}

public class ClientCmdTest {
    public static void main(String[] args) {
        // ========================================
        // setup
        //=========================================

        IFactory factory = new Factory();
        IUtility utility = factory.createUtility(1);

        //String hostname = "192.168.1.159";
        int port = 44123;
        String ip = "127.0.0.1";
        String uniqueId = utility.readFile(Constants.UNIQUE_ID_FILE);
        String defaultSquareInfo = utility.readFile(Constants.DEFAULT_SQUARE_FILE);
        
        ILogIt logger = factory.createLogger(1, "clientCmdTest.log", utility);       
        
        String text = "u%%%a7075b5b-b91d-4448-a0f9-d9b0bec1a726%%%regalias%%%lightning-server~_~192.168.1.153~_~11111~_~72e28b68-8495-4fb5-9d3e-0b8967d3583e";

        IDialogController dController = new xxMockIDialogController();

        ISquareKeyPair keyPair = new xxMockISquareKeyPair(utility);

        ISquareController squareController = factory.createSquareController(1, utility, dController, logger, keyPair);

        //ISquare square = factory.createSquare(1, defaultSquareInfo, Integer.toString(port), ip, squareController, utility, dController, uniqueId);
        
        //square.runClientFunctions(1);
        // ===============================================================

        //String info, String file, String uniqueId, String[] msg, ISquare square, IUtility utility

        squareController.processRequest(text);


        System.exit(0);
    }
}

public class ClientCmdTest {
    public static void main(String[] args) {
        // ========================================
        // setup
        //=========================================

        IUtility utility = Factory.createUtility(1);

        //String hostname = "192.168.1.159";
        int port = 44123;
        String ip = "127.0.0.1";
        String uniqueId = utility.readFile(Constants.UNIQUE_ID_FILE);
        String defaultSquareInfo = utility.readFile(Constants.DEFAULT_SQUARE_FILE);
        
        ILogIt logger = Factory.createLogger(1, "clientCmdTest.log", utility);       
        
        String text = "u%%%a7075b5b-b91d-4448-a0f9-d9b0bec1a726%%%read%%%1611017979985%%%51f7070e-9ebb-429c-a3a6-f847b821451e";

        IDialogController dController = new xxMockIDialogController();

        ISquareController squareController = Factory.createSquareController(1, utility, dController, logger);

        ISquare square = Factory.createSquare(1, defaultSquareInfo, Integer.toString(port), ip, squareController, utility, dController, uniqueId);

        // ===============================================================

        SquareResponse response = squareController.processRequest(text);

        String time = response.getMessage();

        //IClient client = Factory.createClient(1, hostname, port, "a7075b5b-b91d-4448-a0f9-d9b0bec1a726");
        //String time = client.sendMessage(text, false);

        logger.logInfo(time);

        System.exit(0);
    }
}

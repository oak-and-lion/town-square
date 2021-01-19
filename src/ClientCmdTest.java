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
        
        String text = "u%%%a7075b5b-b91d-4448-a0f9-d9b0bec1a726%%%read%%%1611075933936%%%51f7070e-9ebb-429c-a3a6-f847b821451e";

        IDialogController dController = new xxMockIDialogController();

        ISquareController squareController = Factory.createSquareController(1, utility, dController, logger);

        //Factory.createSquare(1, defaultSquareInfo, Integer.toString(port), ip, squareController, utility, dController, uniqueId);

        // ===============================================================

        //String info, String file, String uniqueId, String[] msg, ISquare square, IUtility utility

        SquareResponse response = squareController.processRequest(text);

        String time = "200:" + response.getMessage();
        String[] msg = time.split(Constants.COLON);

        ISquare square = new xxMockSquare();
        IMemberPostsThread mt = new MemberPostsThread("john law~_~MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApsRCab4SDmN1FjBdy11rlIUJg5GbczLnj4dMrqzdVZ7zSYThihSdRHx1HcU+gejwwP/q28vuoClFIOj4kOr73By3UUBWdZLRhOH7NcpLCpiIRINU0poHIHwkmM6L4PdvmUTP099VCmyNlndI/AesCD/ZzSZJDsIyuY36MMJNnjbsHd26tFBDCKqIDRixWUPOR+FX+539YQqGgU3RIBF9uNc6T0aWqijNIlMbMxAOMiUFWbDt+wqQWY6sBgPDVWBZqvXEg+CwzdlUYB5LGbGjwb/xyMiUcCkef14faYsCLu6lXQaRQbKgcmWseQPllhyGwwoyylFdx9GFhT2i1VYWSwIDAQAB~_~192.168.105.48~_~44423~_~d1de5968-814d-4a40-8693-542008c80e1c", "my_square.posts", "123", new String[1], square, utility);
        mt.processPostData(msg);

        ISquareKeyPair keys = Factory.createSquareKeyPair(1);
        keys.setPrivateKeyFromBase64(utility.readFile(Constants.PRIVATE_KEY_FILE));

        String key = keys.decryptFromBase64(msg[0]);

        String f = utility.decrypt(msg[1], key);

        byte[] file = utility.convertFromBase64(f);

        FileWriteResponse result = utility.writeBinaryFile("test.jpg", file);

        logger.logInfo(result.toString());
        //IClient client = Factory.createClient(1, hostname, port, "a7075b5b-b91d-4448-a0f9-d9b0bec1a726");
        //String time = client.sendMessage(text, false);

        logger.logInfo(time);

        System.exit(0);
    }
}

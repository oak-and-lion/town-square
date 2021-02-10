import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

public class CommandWorkerCreateClone extends CommandWorkerBase implements ICommandWorker {
    private IFactory factory;

    public CommandWorkerCreateClone(IUtility utility, ISquare square, IDialogController parent, IFactory factory) {
        super(utility, square, parent);
        this.factory = factory;
    }

    public List<BooleanString> doWork(String commandArgs) {
        ArrayList<BooleanString> result = new ArrayList<>();
        result.add(createClone(commandArgs));
        return result;
    }

    private BooleanString createClone(String commandArgs) {
        String[] args = commandArgs.split(Constants.COMMAND_DATA_SEPARATOR);
        String password = args[0];
        String address = args[2];
        String port = args[3];
        ICommandWorker pauseWorker = factory.createCommandWorker(Constants.PAUSE_COMMAND, utility, square, parent);
        ICommandWorker unpauseWorker = factory.createCommandWorker(Constants.UNPAUSE_COMMAND, utility, square, parent);
        ISquareWorker registerAlias = factory.createSquareWorker(Constants.REGISTER_ALIAS_COMMAND, utility, parent, square.getController().getLogger());
        BooleanString result = new BooleanString(false, Constants.MALFORMED_REQUEST_MESSAGE);

        // decrypt the dna file using the password
        StringBuilder tempPass = new StringBuilder(password);
        if (password.length() < Constants.ENCRYPTION_KEY_LENGTH) {
            for (int x = password.length(); x < Constants.ENCRYPTION_KEY_LENGTH; x++) {
                tempPass.append(Constants.UNDERSCORE);
            }
        }
        String data = utility.decrypt(utility.readFile(utility.concatStrings(square.getSafeLowerName(), Constants.DNA_FILE_EXT)),
                tempPass.toString());
        // if successful:
        if (data.equals(Constants.CLONE_CHALLENGE)) {
            boolean alreadyPaused = utility.checkFileExists(utility.concatStrings(square.getSafeLowerName(), Constants.PAUSE_FILE_EXT));

            if (!alreadyPaused) {
                // pause the square while creating the clone package
                pauseWorker.doWork(Constants.EMPTY_STRING);
            }

            ArrayList<String> regAlias = new ArrayList<>();
            regAlias.add(Constants.UNENCRYPTED_FLAG);
            regAlias.add(square.getInvite());
            regAlias.add(Constants.REGISTER_ALIAS_COMMAND);
            StringBuilder temp = new StringBuilder();
            temp.append(Constants.NULL_TEXT);
            temp.append(Constants.FILE_DATA_SEPARATOR);
            temp.append(address);
            temp.append(Constants.FILE_DATA_SEPARATOR);
            temp.append(port);
            temp.append(Constants.FILE_DATA_SEPARATOR);
            temp.append(utility.readFile(Constants.UNIQUE_ID_FILE));
            regAlias.add(temp.toString());
            registerAlias.doWork(square, regAlias.toArray(new String[regAlias.size()]));

            utility.deleteFile(utility.concatStrings(square.getSafeLowerName(), Constants.CLONE_FILE_EXT));
            // zip up the files
            ArrayList<String> srcFiles = new ArrayList<>();
            srcFiles.add(Constants.DEFAULT_NAME_FILE);
            srcFiles.add(Constants.UNIQUE_ID_FILE);
            srcFiles.add(utility.concatStrings(square.getSafeLowerName(), Constants.SQUARE_FILE_EXT));
            srcFiles.add(utility.concatStrings(square.getSafeLowerName(), Constants.MEMBERS_FILE_EXT));
            srcFiles.add(utility.concatStrings(square.getSafeLowerName(), Constants.POSTS_FILE_EXT));
            srcFiles.add(utility.concatStrings(square.getSafeLowerName(), Constants.ALIAS_FILE_EXT));
            srcFiles.add(Constants.PRIVATE_KEY_FILE);
            srcFiles.add(Constants.PUBLIC_KEY_FILE);
            try (FileOutputStream fos = new FileOutputStream(utility.concatStrings(square.getSafeLowerName(), Constants.TEMP_FILE_EXT))) {

                ZipOutputStream zipOut = new ZipOutputStream(fos);
                for (String srcFile : srcFiles) {
                    utility.addToZip(srcFile, zipOut);
                }
                zipOut.close();
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            // encrypt it using the password
            byte[] zippedData = utility.readBinaryFile(utility.concatStrings(square.getSafeLowerName(), Constants.TEMP_FILE_EXT));

            String zippedB64 = utility.convertToBase64(zippedData);

            String encrypted = utility.encrypt(zippedB64, tempPass.toString());

            result = new BooleanString(true, encrypted);

            if (!alreadyPaused) {
                // unpause square
                unpauseWorker.doWork(Constants.EMPTY_STRING);
            }
        }

        // if unsuccessful:
        // send back a giant fuck you encrypted
        return result;
    }
}

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

    private BooleanString createClone(String password) {
        ICommandWorker pauseWorker = factory.createCommandWorker(Constants.PAUSE_COMMAND, utility, square, parent);
        ICommandWorker unpauseWorker = factory.createCommandWorker(Constants.UNPAUSE_COMMAND, utility, square, parent);
        BooleanString result = new BooleanString(false, Constants.MALFORMED_REQUEST_MESSAGE);

        // decrypt the dna file using the password
        StringBuilder tempPass = new StringBuilder(password);
        if (password.length() < Constants.ENCRYPTION_KEY_LENGTH) {
            for (int x = password.length(); x < Constants.ENCRYPTION_KEY_LENGTH; x++) {
                tempPass.append(Constants.UNDERSCORE);
            }
        }
        String data = utility.decrypt(utility.readFile(square.getSafeLowerName() + Constants.DNA_FILE_EXT),
                tempPass.toString());
        // if successful:
        if (data.equals(Constants.CLONE_CHALLENGE)) {
            boolean alreadyPaused = utility.checkFileExists(square.getSafeLowerName() + Constants.PAUSE_FILE_EXT);

            if (!alreadyPaused) {
                // pause the square while creating the clone package
                pauseWorker.doWork(Constants.EMPTY_STRING);
            }

            utility.deleteFile(square.getSafeLowerName() + Constants.CLONE_FILE_EXT);
            // zip up the files
            ArrayList<String> srcFiles = new ArrayList<>();
            srcFiles.add(Constants.DEFAULT_NAME_FILE);
            srcFiles.add(Constants.UNIQUE_ID_FILE);
            srcFiles.add(square.getSafeLowerName() + Constants.SQUARE_FILE_EXT);
            srcFiles.add(square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT);
            srcFiles.add(square.getSafeLowerName() + Constants.POSTS_FILE_EXT);
            srcFiles.add(square.getSafeLowerName() + Constants.ALIAS_FILE_EXT);
            srcFiles.add(Constants.PRIVATE_KEY_FILE);
            srcFiles.add(Constants.PUBLIC_KEY_FILE);
            try (FileOutputStream fos = new FileOutputStream(square.getSafeLowerName() + Constants.TEMP_FILE_EXT)) {

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
            byte[] zippedData = utility.readBinaryFile(square.getSafeLowerName() + Constants.TEMP_FILE_EXT);

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
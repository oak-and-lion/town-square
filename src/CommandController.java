import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;

import javafx.application.Platform;

public class CommandController implements ICommandController {
    private IUtility utility;
    private IDialogController parent;
    private ArrayList<String> commands;
    private IFactory factory;

    public CommandController(IUtility utility, IDialogController parent, IFactory factory) {
        this.utility = utility;
        this.parent = parent;
        this.factory = factory;
        this.commands = new ArrayList<>();
        commands.add(Constants.ABOUT_COMMAND);
        commands.add(Constants.BLOCK_COMMAND);
        commands.add(Constants.CLONE_COMMAND);
        commands.add(Constants.DNA_COMMAND);
        commands.add(Constants.EXPOSE_COMMAND);
        commands.add(Constants.HELP_COMMAND);
        commands.add(Constants.HIDE_COMMAND);
        commands.add(Constants.LICENSE_COMMAND);
        commands.add(Constants.PAUSE_COMMAND);
        commands.add(Constants.UNBLOCK_COMMAND);
        commands.add(Constants.UNPAUSE_COMMAND);
    }

    public BooleanString[] processCommand(String command, ISquare square) {
        if (!command.startsWith(Constants.COMMAND_PREFIX)) {
            return new BooleanString[] { new BooleanString(false, Constants.MALFORMED_REQUEST_MESSAGE) };
        }

        command = command.replace(Constants.COMMAND_PREFIX, Constants.EMPTY_STRING);
        String[] commandBreakdown = command.split(Constants.SPACE);

        String cmd = commandBreakdown[0].replace(Constants.SPACE, Constants.EMPTY_STRING).toLowerCase().trim();

        ArrayList<BooleanString> result = new ArrayList<>();
        if (cmd.equals(Constants.BLOCK_COMMAND)) {
            String[] users = commandBreakdown[1].split(Constants.SEMI_COLON);
            for (String user : users) {
                result.add(new BooleanString(blockUser(user, square), Constants.EMPTY_STRING));
            }
        } else if (cmd.equals(Constants.PAUSE_COMMAND)) {
            pauseSquare(square);
            result.add(new BooleanString(true, Constants.EMPTY_STRING));
        } else if (cmd.equals(Constants.UNPAUSE_COMMAND)) {
            unPauseSquare(square);
            result.add(new BooleanString(true, Constants.EMPTY_STRING));
        } else if (cmd.equals(Constants.ABOUT_COMMAND)) {
            parent.showAbout();
            result.add(new BooleanString(true, Constants.EMPTY_STRING));
        } else if (cmd.equals(Constants.UNBLOCK_COMMAND)) {
            String[] users = commandBreakdown[1].split(Constants.SEMI_COLON);
            for (String user : users) {
                result.add(new BooleanString(unblockUser(user, square), Constants.EMPTY_STRING));
            }
        } else if (cmd.equals(Constants.HELP_COMMAND)) {
            parent.showList(commands.toArray(new String[commands.size()]), Constants.COMMANDS_TITLE,
                    Constants.COMMANDS_HEADER);
            result.add(new BooleanString(true, Constants.EMPTY_STRING));
        } else if (cmd.equals(Constants.HIDE_COMMAND)) {
            parent.getParent().hideServer();
            result.add(new BooleanString(true, Constants.EMPTY_STRING));
        } else if (cmd.equals(Constants.EXPOSE_COMMAND)) {
            parent.getParent().exposeServer();
            result.add(new BooleanString(true, Constants.EMPTY_STRING));
        } else if (cmd.equals(Constants.LICENSE_COMMAND)) {
            parent.showLicense();
            result.add(new BooleanString(true, Constants.EMPTY_STRING));
        } else if (cmd.equals(Constants.DNA_COMMAND)) {
            result.add(new BooleanString(createDNA(commandBreakdown[1], square), Constants.EMPTY_STRING));
        } else if (cmd.equals(Constants.CLONE_COMMAND)) {
            result.add(createClone(commandBreakdown[1], square));
        } else if (cmd.equals(Constants.SEND_CLONE_COMMAND)) {
            result.add(getClone(commandBreakdown, square));
        } else {
            result.add(new BooleanString(false, Constants.EMPTY_STRING));
        }

        return result.toArray(new BooleanString[result.size()]);
    }

    public boolean createDNA(String password, ISquare square) {
        StringBuilder tempPass = new StringBuilder(password);
        if (password.length() < Constants.ENCRYPTION_KEY_LENGTH) {
            for (int x = password.length(); x < Constants.ENCRYPTION_KEY_LENGTH; x++) {
                tempPass.append(Constants.UNDERSCORE);
            }
        }
        String data = utility.encrypt(Constants.CLONE_CHALLENGE, tempPass.toString());
        FileWriteResponse result = utility.writeFile(square.getSafeLowerName() + Constants.DNA_FILE_EXT, data);
        return result.isSuccessful();
    }

    public BooleanString createClone(String password, ISquare square) {
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
            // pause the square while creating the clone package
            pauseSquare(square);

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
            // unpause square
            unPauseSquare(square);
        }

        // if unsuccessful:
        // send back a giant fuck you encrypted
        return result;
    }

    public BooleanString getClone(String[] args, ISquare square) {
        IClient client = factory.createClient(Constants.BASE_CLIENT, args[1], Integer.valueOf(args[2]), args[3]);
        String pkey = client.sendMessage(Constants.REQUEST_PUBLIC_KEY_COMMAND, false);
        SquareResponse resp = new SquareResponse(pkey);
        if (resp.getCode().equals(Constants.OK_RESULT)) {
            ISquareKeyPair keyPair = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
            keyPair.setPublicKeyFromBase64(resp.getMessage());
            String key = utility.generateRandomString(Constants.ENCRYPTION_KEY_LENGTH);
            String e = utility.encrypt(Constants.CLONE_COMMAND + Constants.COMMAND_DATA_SEPARATOR + args[4], key);
            String f = keyPair.encryptToBase64(key);
            String result = client.sendMessage(f + Constants.COMMAND_DATA_SEPARATOR + e, true);
            SquareResponse cloneResponse = new SquareResponse(result);
            if (cloneResponse.getCode().equals(Constants.OK_RESULT)) {
                StringBuilder pwdPadded = new StringBuilder(args[4]);
                for (int x = args[4].length(); x < Constants.ENCRYPTION_KEY_LENGTH; x++) {
                    pwdPadded.append(Constants.UNDERSCORE);
                }
                String b64 = utility.decrypt(cloneResponse.getMessage(), pwdPadded.toString());
                byte[] data = utility.convertFromBase64(b64);

                utility.deleteFile(square.getSafeLowerName() + Constants.CLONE_FILE_EXT);
                utility.writeBinaryFile(square.getSafeLowerName() + Constants.CLONE_FILE_EXT, data);
                parent.showCloneMessage();
                return new BooleanString(true, square.getSafeLowerName() + Constants.CLONE_FILE_EXT);
            }
        }

        return new BooleanString(false, Constants.EMPTY_STRING);
    }

    public boolean pauseSquare(ISquare square) {
        if (square == null) {
            return false;
        }

        FileWriteResponse result = utility.writeFile(square.getSafeLowerName() + Constants.PAUSE_FILE_EXT,
                Constants.PAUSE_FILE_CONTENTS);

        if (result.isSuccessful() && parent.isGui()) {
            try {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        parent.updatePauseNotification(square, true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result.isSuccessful();
    }

    public boolean unPauseSquare(ISquare square) {
        if (square == null) {
            return false;
        }

        boolean result = utility.deleteFile(square.getSafeLowerName() + Constants.PAUSE_FILE_EXT);

        if (result && parent.isGui()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    parent.updatePauseNotification(square, false);
                }
            });
        }

        return result;
    }

    public boolean blockUser(String user, ISquare square) {
        if (square == null) {
            return false;
        }
        if (user == null) {
            return false;
        }
        user = user.trim();
        if (user.equals(Constants.EMPTY_STRING)) {
            return false;
        }
        String[] results = utility.searchFile(square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT, user,
                Constants.SEARCH_STARTS_WITH);
        if (results.length > 0) {
            String[] memberInfo = results[0].split(Constants.FILE_DATA_SEPARATOR);
            String[] alreadyBlocked = utility.searchFile(square.getSafeLowerName() + Constants.BLOCK_FILE_EXT,
                    memberInfo[4], Constants.SEARCH_CONTAINS);
            if (alreadyBlocked.length < 1) {
                int lines = utility.countLinesInFile(square.getSafeLowerName() + Constants.BLOCK_FILE_EXT);
                String newLine = Constants.EMPTY_STRING;
                if (lines > 0) {
                    newLine = Constants.NEWLINE;
                }
                FileWriteResponse response = utility.appendToFile(square.getSafeLowerName() + Constants.BLOCK_FILE_EXT,
                        newLine + memberInfo[4]);
                return response.isSuccessful();
            }
            return false;
        }

        return false;
    }

    public boolean unblockUser(String user, ISquare square) {
        if (square == null) {
            return false;
        }
        if (user == null) {
            return false;
        }
        user = user.trim();
        if (user.equals(Constants.EMPTY_STRING)) {
            return false;
        }

        String[] results = utility.searchFile(square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT, user,
                Constants.SEARCH_STARTS_WITH);
        if (results.length > 0) {
            String[] memberInfo = results[0].split(Constants.FILE_DATA_SEPARATOR);
            String[] blockedMembers = utility.readFile(square.getSafeLowerName() + Constants.BLOCK_FILE_EXT)
                    .split(Constants.READ_FILE_DATA_SEPARATOR);
            StringBuilder output = new StringBuilder();
            boolean first = true;
            String newline = Constants.EMPTY_STRING;
            for (String blockedMember : blockedMembers) {
                if (!blockedMember.equals(memberInfo[4])) {
                    output.append(newline + blockedMember);
                }
                if (first) {
                    first = false;
                    newline = Constants.NEWLINE;
                }
            }
            FileWriteResponse response = utility.writeFile(square.getSafeLowerName() + Constants.BLOCK_FILE_EXT,
                    output.toString());
            return response.isSuccessful();
        }
        return false;
    }
}

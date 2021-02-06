import java.util.ArrayList;

import javafx.application.Platform;

public class CommandController implements ICommandController {
    private IUtility utility;
    private IDialogController parent;
    private ArrayList<String> commands;

    public CommandController(IUtility utility, IDialogController parent) {
        this.utility = utility;
        this.parent = parent;
        this.commands = new ArrayList<>();
        commands.add(Constants.ABOUT_COMMAND);
        commands.add(Constants.BLOCK_COMMAND);
        commands.add(Constants.EXPOSE_COMMAND);
        commands.add(Constants.HELP_COMMAND);
        commands.add(Constants.HIDE_COMMAND);
        commands.add(Constants.LICENSE_COMMAND);
        commands.add(Constants.PAUSE_COMMAND);
        commands.add(Constants.UNBLOCK_COMMAND);
        commands.add(Constants.UNPAUSE_COMMAND);
    }

    public Boolean[] processCommand(String command, ISquare square) {
        if (!command.startsWith(Constants.COMMAND_PREFIX)) {
            return new Boolean[] { false };
        }

        command = command.replace(Constants.COMMAND_PREFIX, Constants.EMPTY_STRING);
        String[] commandBreakdown = command.split(Constants.SPACE);

        String cmd = commandBreakdown[0].replace(Constants.SPACE, Constants.EMPTY_STRING).toLowerCase().trim();

        ArrayList<Boolean> result = new ArrayList<>();
        if (cmd.equals(Constants.BLOCK_COMMAND)) {
            String[] users = commandBreakdown[1].split(Constants.SEMI_COLON);
            for (String user : users) {
                result.add(blockUser(user, square));
            }
        } else if (cmd.equals(Constants.PAUSE_COMMAND)) {
            pauseSquare(square);
        } else if (cmd.equals(Constants.UNPAUSE_COMMAND)) {
            unPauseSquare(square);
        } else if (cmd.equals(Constants.ABOUT_COMMAND)) {
            parent.showAbout();
        } else if (cmd.equals(Constants.UNBLOCK_COMMAND)) {
            String[] users = commandBreakdown[1].split(Constants.SEMI_COLON);
            for (String user : users) {
                result.add(unblockUser(user, square));
            }
        } else if (cmd.equals(Constants.HELP_COMMAND)) {
            parent.showList(commands.toArray(new String[commands.size()]), Constants.COMMANDS_TITLE,
                    Constants.COMMANDS_HEADER);
        } else if (cmd.equals(Constants.HIDE_COMMAND)) {
            parent.getParent().hideServer();
        } else if (cmd.equals(Constants.EXPOSE_COMMAND)) {
            parent.getParent().exposeServer();
        } else if (cmd.equals(Constants.LICENSE_COMMAND)) {
            parent.showLicense();
        } else {
            result.add(false);
        }

        return result.toArray(new Boolean[result.size()]);
    }

    public boolean pauseSquare(ISquare square) {
        if (square == null) {
            return false;
        }

        FileWriteResponse result = utility.writeFile(square.getSafeLowerName() + Constants.PAUSE_FILE_EXT,
                Constants.PAUSE_FILE_CONTENTS);

        if (result.isSuccessful()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    parent.updatePauseNotification(square, true);
                }
            });
        }

        return result.isSuccessful();
    }

    public boolean unPauseSquare(ISquare square) {
        if (square == null) {
            return false;
        }

        boolean result = utility.deleteFile(square.getSafeLowerName() + Constants.PAUSE_FILE_EXT);

        if (result) {
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
            String[] blockedMembers = utility.readFile(square.getSafeLowerName() + Constants.BLOCK_FILE_EXT).split(Constants.READ_FILE_DATA_SEPARATOR);
            StringBuilder output = new StringBuilder();
            boolean first = true;
            String newline = Constants.EMPTY_STRING;
            for(String blockedMember : blockedMembers) {
                if (!blockedMember.equals(memberInfo[4])) {
                    output.append(newline + blockedMember);
                }
                if (first) {
                    first = false;
                    newline = Constants.NEWLINE;
                }
            }
            FileWriteResponse response = utility.writeFile(square.getSafeLowerName() + Constants.BLOCK_FILE_EXT, output.toString());
            return response.isSuccessful();
        }
        return false;
    }
}

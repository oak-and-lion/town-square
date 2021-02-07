import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class SquareController implements ISquareController {
    private IUtility utility;
    private IDialogController sampleController;
    private ISquareKeyPair keys;
    private ILogIt logger;
    private IFactory factory;

    public SquareController(IUtility mainUtility, IDialogController controller, ILogIt logger, ISquareKeyPair keyPair,
            IFactory factory) {
        utility = mainUtility;
        sampleController = controller;
        keys = keyPair;
        this.logger = logger;
        this.factory = factory;
    }

    public SquareResponse processRequest(String request) {
        SquareResponse result = new SquareResponse();
        boolean okToProcess = true;

        // command structure
        // 0 == encryption flag
        // 1 == square invite id
        // 2 == command
        // 3+ == command arguments
        String[] split = request.split(Constants.COMMAND_DATA_SEPARATOR);
        if (split.length > 2) {
            String[] newSplit;
            if (split[0].equals(Constants.ENCRYPTION_FLAG)) {
                newSplit = decryptArray(split);
                if (newSplit.length == 0) {
                    result.setResponse(
                            buildResult(Constants.MALFORMED_REQUEST_RESULT, Constants.MALFORMED_REQUEST_MESSAGE));
                    okToProcess = false;
                }
            } else {
                newSplit = split;
            }

            if (okToProcess) {
                result = processRequestCommand(newSplit);
            }
        }

        return result;
    }

    private String[] decryptArray(String[] split) {
        ArrayList<String> result = new ArrayList<>();

        ISquare square = sampleController.getSquareByInvite(split[1]);
        if (square != null) {
            String temp = keys.decryptFromBase64(split[2]).trim();
            logger.logInfo(split[3]);
            try {
                String raw = utility.decrypt(split[3], temp);

                String[] data = raw.split(Constants.COMMAND_DATA_SEPARATOR);

                result.add(split[0]);
                result.add(split[1]);
                result.addAll(Arrays.asList(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result.toArray(new String[result.size()]);
    }

    private SquareResponse processRequestCommand(String[] split) {
        SquareResponse result = new SquareResponse();

        ISquare square = sampleController.getSquareByInvite(split[1]);

        if (square == null) {
            result.setResponse(buildResult(Constants.MALFORMED_REQUEST_RESULT, Constants.MALFORMED_REQUEST_MESSAGE));
            return result;
        }

        String command = split[2].trim();

        if (command.equals(Constants.JOIN_COMMAND)) {
            result.setResponse(processJoinRequest(split, square));
        } else if (command.equals(Constants.POST_COMMAND)) {
            result.setResponse(processPostMessage(split, square));
        } else if (command.equals(Constants.READ_COMMAND)) {
            result.setResponse(buildResult(Constants.OK_RESULT, getPosts(square, split)));
        } else if (command.equals(Constants.MEMBER_COMMAND)) {
            result.setResponse(buildResult(Constants.OK_RESULT, getMembers(square, split)));
        } else if (command.equals(Constants.READ_ALIAS_COMMAND)) {
            result.setResponse(buildResult(Constants.OK_RESULT, getAliases(square, split)));
        } else if (command.equals(Constants.REQUEST_PUBLIC_KEY_COMMAND)) {
            result.setResponse(processPublicKeyMessage());
        } else if (command.equals(Constants.REQUEST_FILE_COMMAND)) {
            result.setResponse(buildResult(Constants.OK_RESULT, processFileGetMessage(square, split)));
        } else if (command.equals(Constants.CHECK_VERSION_COMMAND)) {
            result.setResponse(buildResult(Constants.OK_RESULT, Constants.VERSION));
        } else if (command.equals(Constants.REGISTER_ALIAS_COMMAND)) {
            result.setResponse(buildResult(Constants.OK_RESULT, registerAlias(square, split)));
        } else if (command.equals(Constants.CLONE_COMMAND)) {
            result.setResponse(buildClone(square, split));
        } else if (command.equals(Constants.SEND_CLONE_COMMAND)) {
            result.setResponse(packageClone(square, split));
        } else if (command.equals(Constants.ACK_COMMAND)) {
            result.setResponse(buildResult(Constants.OK_RESULT, Constants.ACK_BACK));
        } else if (command.equals(Constants.FAILURE_COMMAND)) {
            result.setResponse(buildResult(Constants.DECRYPTION_FAILURE_RESULT, Constants.DECRYPTION_FAILURE_MESSAGE));
        } else {
            result.setResponse(buildResult(Constants.UNKNOWN_COMMAND_RESULT, command));
        }

        return result;
    }

    private String processCommand(String data, String memberId, String file, ISquare square) {
        String result;

        if (square != null) {
            FileWriteResponse b;

            if (utility.checkFileExists(file)) {
                b = utility.appendToFile(file, Constants.NEWLINE + data + Constants.DATA_SEPARATOR + memberId);
            } else {
                b = utility.writeFile(file, data + Constants.DATA_SEPARATOR + memberId);
            }

            if (b.isSuccessful()) {
                result = buildResult(Constants.OK_RESULT,
                        Constants.ADDED_MESSAGE + Constants.COLON + data + Constants.COLON
                                + sampleController.getDefaultName() + Constants.UNDERSCORE + square.getSafeLowerName()
                                + Constants.COLON + Integer.toString(b.getLineCount()));
            } else {
                result = buildResult(Constants.INTERNAL_ERROR_RESULT, Constants.ADDING_MEMBER_MESSAGE);
            }
        } else {
            result = buildResult(Constants.UNKNOWN_SQUARE_RESULT, Constants.UNKNOWN_SQUARE_MESSAGE);
        }

        return result;
    }

    private String packageClone(ISquare square, String[] args) {
        return Constants.EMPTY_STRING;
    }

    private String buildClone(ISquare square, String[] args) {
        IDialogController controller = square.getSampleController();
        ICommandController cmdController = controller.getCommandController();
        BooleanString[] result = cmdController.processCommand(Constants.FORWARD_SLASH + args[2] + Constants.SPACE + args[3], square);
        if (result.length > 0 && result[0].getBoolean()) {
            return buildResult(Constants.OK_RESULT, result[0].getString());
        }
        return buildResult(Constants.MALFORMED_REQUEST_RESULT, Constants.MALFORMED_REQUEST_MESSAGE);
    }

    private String processJoinRequest(String[] args, ISquare square) {
        // command arguments
        // 3 == desired name
        // 4 == member public key
        // 5 == ip address
        // 6 == port
        // 7 == unique id of member

        if (args.length == 8) {
            String file = square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT;
            String[] sameNames = utility.searchFile(file, args[3], Constants.SEARCH_STARTS_WITH);
            String[] sameIds = utility.searchFile(file, args[7], Constants.SEARCH_CONTAINS);
            boolean previousLeave = checkPreviousLeave(sameIds);
            processPreviousLeave(previousLeave, file, args);
            String registeredName = args[3];
            if (sameIds.length < 1 || previousLeave) {
                if (sameNames.length > 0) {
                    registeredName += Constants.PERCENT + Integer.toString(sameNames.length);
                }

                String data = args[4] + Constants.DATA_SEPARATOR + args[5] + Constants.DATA_SEPARATOR + args[6]
                        + Constants.DATA_SEPARATOR + args[7];

                return processCommand(registeredName, data, file, square);
            } else {
                return buildResult(Constants.ALREADY_REGISTERED_RESULT,
                        Constants.ALREADY_REGISTERED_MESSAGE + Constants.COLON + Constants.DASH + Constants.COLON
                                + sampleController.getDefaultName() + Constants.UNDERSCORE + square.getSafeLowerName()
                                + Constants.COLON + Constants.NO_ROWS);
            }
        }

        return buildResult(Constants.MALFORMED_REQUEST_RESULT, Constants.MALFORMED_REQUEST_MESSAGE);
    }

    private void processPreviousLeave(boolean previousLeave, String file, String[] args) {
        if (previousLeave) {
            String[] members = utility.readFile(file).split(Constants.READ_FILE_DATA_SEPARATOR);
            StringBuilder result = new StringBuilder();
            for (String member : members) {
                if (!member.contains(args[7])) {
                    result.append(member);
                }
            }
            utility.writeFile(file, result.toString());
        }
    }

    private boolean checkPreviousLeave(String[] sameIds) {
        boolean previousLeave = false;
        for (String sameId : sameIds) {
            if (sameId.startsWith(Constants.STAR)) {
                previousLeave = true;
                break;
            }
        }

        return previousLeave;
    }

    private String processPostMessage(String[] args, ISquare square) {
        // command arguments
        // 3 == message to post
        // 4 == member id
        if (args.length == 5) {
            long currentMillis = System.currentTimeMillis();
            String file = square.getSafeLowerName() + Constants.POSTS_FILE_EXT;
            String memberFile = square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT;
            String[] sameNames = utility.searchFile(memberFile, args[4], Constants.SEARCH_CONTAINS);
            if (sameNames.length > 0) {
                return processCommand(Long.toString(currentMillis) + Constants.DATA_SEPARATOR + args[3], args[4], file,
                        square);
            } else {
                return buildResult(Constants.FORBIDDEN_RESULT, Constants.FORBIDDEN_MESSAGE);
            }
        }

        return buildResult(Constants.MALFORMED_REQUEST_RESULT, Constants.MALFORMED_REQUEST_MESSAGE);
    }

    private String getPosts(ISquare square, String[] split) {
        // command arguments
        // 3 == last known timestamp
        // 4 == requesting member id

        if (split.length != 5) {
            return Constants.EMPTY_STRING;
        }

        String start = split[3];
        String memberId = split[4];

        if (checkSquareAccess(square, memberId)) {
            String file = square.getSafeLowerName() + Constants.POSTS_FILE_EXT;
            String memberFile = square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT;
            boolean getEntireFile = Long.valueOf(start) > -1 ? Constants.NOT_FOUND_RETURN_ZERO
                    : !Constants.NOT_FOUND_RETURN_ZERO;
            if (getEntireFile != Constants.NOT_FOUND_RETURN_ZERO) {
                logger.logInfo("Need whole file");
            }
            int firstRow = utility.findFirstOccurence(file, start, Constants.SEARCH_STARTS_WITH, getEntireFile);
            String posts = utility.readFile(file, firstRow);

            String[] members = utility.readFile(memberFile).split(Constants.COMMAND_DATA_SEPARATOR);

            ArrayList<String> memberIds = new ArrayList<>();
            ArrayList<String> memberNames = new ArrayList<>();

            for (int x = 0; x < members.length; x++) {
                String[] data = members[x].split(Constants.DATA_SEPARATOR);
                if (data[4].equals(split[4])) {
                    memberNames.add(data[0]);
                    memberIds.add(data[1]);
                }
            }

            if (!memberNames.isEmpty()) {
                ISquareKeyPair tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
                tempKeys.setPublicKeyFromBase64(memberIds.get(0));
                String password = utility.generateRandomString(16);
                StringBuilder temp = new StringBuilder();
                temp.append(utility.encrypt(posts, password));
                posts = tempKeys.encryptToBase64(password) + Constants.COMMAND_DATA_SEPARATOR + temp.toString();
                return posts;
            }

            return Constants.EMPTY_STRING;
        }

        return Constants.EMPTY_STRING;
    }

    private String processPublicKeyMessage() {
        String key = utility.readFile(Constants.PUBLIC_KEY_FILE);
        return buildResult(Constants.OK_RESULT, key);
    }

    private String processFileGetMessage(ISquare square, String[] split) {
        // command arguments
        // 3 == file name
        // 4 == requesting member id

        if (split.length != 5) {
            return Constants.EMPTY_STRING;
        }

        String fileRequest = split[3].trim().toLowerCase();
        if (fileRequest.endsWith(Constants.KEY_FILE_EXT) || fileRequest.endsWith(Constants.BLOCK_FILE_EXT)
                || fileRequest.endsWith(Constants.ALIAS_FILE_EXT) || fileRequest.endsWith(Constants.MEMBERS_FILE_EXT)
                || fileRequest.endsWith(Constants.POSTS_FILE_EXT) || fileRequest.endsWith(Constants.LOG_FILE_EXT)
                || fileRequest.endsWith(Constants.ID_FILE_EXT) || fileRequest.endsWith(Constants.JAR_FILE_EXT)
                || fileRequest.endsWith(Constants.TXT_FILE_EXT) || fileRequest.endsWith(Constants.SH_FILE_EXT)
                || fileRequest.endsWith(Constants.BAT_FILE_EXT) || fileRequest.endsWith(Constants.SQUARE_FILE_EXT)) {
            return Constants.GO_AWAY;
        }

        String result = Constants.EMPTY_STRING;
        String memberFile = square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT;

        try (InputStream stream = new FileInputStream(split[3])) {
            result = utility.convertToBase64(stream.readAllBytes());

            String[] members = utility.readFile(memberFile).split(Constants.COMMAND_DATA_SEPARATOR);

            ArrayList<String> memberIds = new ArrayList<>();
            ArrayList<String> memberNames = new ArrayList<>();

            for (int x = 0; x < members.length; x++) {
                String[] data = members[x].split(Constants.DATA_SEPARATOR);
                if (data[4].equals(split[4])) {
                    memberNames.add(data[0]);
                    memberIds.add(data[1]);
                }
            }

            ISquareKeyPair tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
            tempKeys.setPublicKeyFromBase64(memberIds.get(0));
            String password = utility.generateRandomString(16);
            StringBuilder temp = new StringBuilder();
            temp.append(utility.encrypt(result, password));
            result = tempKeys.encryptToBase64(password) + Constants.COMMAND_DATA_SEPARATOR + temp.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String getMembers(ISquare square, String[] split) {
        // command arguments
        // 3 == member id
        if (split.length != 4) {
            return Constants.EMPTY_STRING;
        }

        String memberId = split[3];

        if (checkSquareAccess(square, memberId)) {
            return utility.readFile(square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT);
        }

        return Constants.EMPTY_STRING;
    }

    private String getAliases(ISquare square, String[] split) {
        // command arguments
        // 3 == member id
        if (split.length != 4) {
            return Constants.EMPTY_STRING;
        }

        String memberId = split[3];

        if (checkSquareAccess(square, memberId)) {
            String[] member = utility.searchFile(square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT, memberId,
                    Constants.SEARCH_CONTAINS);
            String[] memberInfo = member[0].split(Constants.FILE_DATA_SEPARATOR);
            ISquareKeyPair tempKeys = factory.createSquareKeyPair(Constants.UTILITY_SQUARE_KEY_PAIR, utility);
            tempKeys.setPublicKeyFromBase64(memberInfo[1]);
            String password = utility.generateRandomString(16);
            StringBuilder temp = new StringBuilder();
            temp.append(
                    utility.encrypt(utility.readFile(square.getSafeLowerName() + Constants.ALIAS_FILE_EXT), password));
            return tempKeys.encryptToBase64(password) + Constants.COMMAND_DATA_SEPARATOR + temp.toString();
        }

        return Constants.EMPTY_STRING;
    }

    private String buildResult(String code, String msg) {
        return code + Constants.COLON + msg;
    }

    private boolean checkSquareAccess(ISquare square, String memberId) {
        String file = square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT;
        int first = utility.findFirstOccurence(file, memberId, Constants.SEARCH_CONTAINS,
                Constants.NOT_FOUND_RETURN_NEG_ONE);

        if (first < 0) {
            first = utility.findFirstOccurence(file, Constants.EXIT_SQUARE_TEXT, Constants.SEARCH_STARTS_WITH,
                    Constants.NOT_FOUND_RETURN_NEG_ONE);
        }
        return (first > -1);
    }

    private String registerAlias(ISquare square, String[] split) {
        // command arguments
        // 3 == member info
        String[] info = split[3].split(Constants.FILE_DATA_SEPARATOR);
        if (!checkSquareAccess(square, info[3])) {
            return Constants.MALFORMED_REQUEST_MESSAGE;
        }
        String file = square.getSafeLowerName() + Constants.ALIAS_FILE_EXT;
        ArrayList<String> memberAliases = new ArrayList<>();
        if (utility.checkFileExists(file)) {
            String[] members = utility.readFile(file).split(Constants.READ_FILE_DATA_SEPARATOR);
            for (String member : members) {
                if (!member.equals(Constants.EMPTY_STRING)) {
                    memberAliases.add(member);
                }
            }
        }

        // info order
        // 0 == null
        // 1 == ip address
        // 2 == port
        // 3 == member id
        return processAlias(info, memberAliases, file);
    }

    private String processAlias(String[] info, ArrayList<String> memberAliases, String file) {
        // command arguments
        // 3 == member id
        String alias = info[3] + Constants.QUESTION_MARK + info[1] + Constants.COLON + info[2];

        boolean found = false;
        int count = 0;
        for (String memberAlias : memberAliases) {
            if (memberAlias.startsWith(info[3]) && !memberAlias.contains(info[1] + Constants.COLON + info[2])) {
                memberAlias += Constants.FORWARD_SLASH + info[1] + Constants.COLON + info[2];
                memberAliases.set(count, memberAlias);
                found = true;
                break;
            } else if (memberAlias.startsWith(info[3]) && memberAlias.contains(info[1] + Constants.COLON + info[2])) {
                return "registered";
            }
            count++;
        }

        if (found) {
            utility.deleteFile(file);
            boolean first = true;
            String newLine = Constants.EMPTY_STRING;
            for (String memberAlias : memberAliases) {
                utility.appendToFile(file, newLine + memberAlias);
                if (first) {
                    newLine = Constants.NEWLINE;
                    first = false;
                }
            }
        } else {
            if (utility.checkFileExists(file)) {
                utility.appendToFile(file, Constants.NEWLINE);
            }
            utility.appendToFile(file, alias);
        }

        return "registered";
    }

    public boolean isHiding() {
        return sampleController.getParent().isHidingServer();
    }
}

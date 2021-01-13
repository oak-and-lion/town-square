import java.util.ArrayList;

public class SquareController implements ISquareController {
    private static final String JOIN_COMMAND = "join";
    private static final String POST_COMMAND = "post";
    private static final String READ_COMMAND = "read";
    private static final String READ_MEMBERS_COMMAND = "members";
    private static final String ACK_COMMAND = "ack";
    private static final String COMMAND_ARG_SEPARATOR = "%%%";
    private static final String DATA_SEPARATOR = "~_~";
    private static final String MEMBER_FILE_EXT = ".members";
    private static final String POST_FILE_EXT = ".posts";
    private static final String NEWLINE = "\n";
    private static final String COLON = ":";
    private static final String PERCENT = "%";
    private static final String EMPTY_STRING = "";
    private static final boolean SEARCH_STARTS_WITH = true;
    private static final boolean SEARCH_CONTAINS = false;
    private static final String FAILURE_COMMAND = "failure";
    private static final String OK_RESULT = "200";
    private static final String INTERNAL_ERROR_RESULT = "500";
    private static final String MALFORMED_REQUEST_RESULT = "401";
    private static final String UNKNOWN_SQUARE_RESULT = "402";
    private static final String FORBIDDEN_RESULT = "403";
    private static final String DECRYPTION_FAILURE_RESULT = "450";
    private static final String ALREADY_REGISTERED_RESULT = "460";
    private static final String UNKNOWN_COMMAND_RESULT = "499";
    private static final String DECRYPTION_FAILURE_MESSAGE = "invalid password";
    private static final String MALFORMED_REQUEST_MESSAGE = "malformed request";
    private static final String ADDED_MESSAGE = "added";
    private static final String ADDING_MEMBER_MESSAGE = "adding member";
    private static final String UNKNOWN_SQUARE_MESSAGE = "unknown square";
    private static final String FORBIDDEN_MESSAGE = "forbidden";
    private static final String ALREADY_REGISTERED_MESSAGE = "already registered";

    Utility utility;
    SampleController sampleController;

    public SquareController(Utility mainUtility, SampleController controller) {
        utility = mainUtility;
        sampleController = controller;
    }

    public SquareResponse processRequest(String request) {
        SquareResponse result = new SquareResponse();

        // command structure
        // 0 == encryption flag
        // 1 == square invite id
        // 2 == command
        // 3+ == command arguments
        String[] split = request.split(COMMAND_ARG_SEPARATOR);
        if (split.length > 2) {
            String[] newSplit;
            if (split[0].equals("e")) {
                newSplit = decryptArray(split);
            } else {
                LogIt.LogInfo("unencrypted");
                newSplit = split;
            }

            result = processRequestCommand(newSplit);
        }

        return result;
    }

    private String[] decryptArray(String[] split) {
        Square square = sampleController.getSquareByInvite(split[1]);
        if (square != null) {
            for (int x = 2; x < split.length; x++) {
                String temp = utility.decrypt(split[x], square.getPassword());
                if (temp != null) {
                    split[x] = temp;
                } else {
                    split[2] = FAILURE_COMMAND;
                    break;
                }
            }
        }

        return split;
    }

    private SquareResponse processRequestCommand(String[] split) {
        SquareResponse result = new SquareResponse();

        Square square = sampleController.getSquareByInvite(split[1]);

        if (square == null) {
            result.setResponse(buildResult(MALFORMED_REQUEST_RESULT, MALFORMED_REQUEST_MESSAGE));
            return result;
        }

        if (split[2].trim().equals(JOIN_COMMAND)) {
            LogIt.LogInfo(split[4]);
            result.setResponse(processJoinRequest(split, square));
        } else if (split[2].trim().equals(POST_COMMAND)) {
            result.setResponse(processPostMessage(split, square));
        } else if (split[2].trim().equals(READ_COMMAND)) {
            result.setResponse(buildResult(OK_RESULT, getPosts(square, split)));
        } else if (split[2].trim().equals(READ_MEMBERS_COMMAND)) {
            result.setResponse(buildResult(OK_RESULT, getMembers(square, split)));
        } else if (split[2].trim().equals(ACK_COMMAND)) {
            result.setResponse(buildResult(OK_RESULT, "ack back"));
        } else if (split[2].equals(FAILURE_COMMAND)) {
            result.setResponse(buildResult(DECRYPTION_FAILURE_RESULT, DECRYPTION_FAILURE_MESSAGE));
        } else {
            result.setResponse(buildResult(UNKNOWN_COMMAND_RESULT, split[2]));
        }

        return result;
    }

    private String processCommand(String data, String memberId, String file, Square square) {
        String result;

        if (square != null) {
            FileWriteResponse b;

            if (utility.checkFileExists(file)) {
                b = utility.appendToFile(file, NEWLINE + data + DATA_SEPARATOR + memberId);
            } else {
                b = utility.writeFile(file, data + DATA_SEPARATOR + memberId);
            }

            if (b.isSuccessful()) {
                result = buildResult(OK_RESULT, ADDED_MESSAGE + COLON + data + COLON + sampleController.getDefaultName()
                        + " - " + square.getName() + COLON + Integer.toString(b.getLineCount()));
            } else {
                result = buildResult(INTERNAL_ERROR_RESULT, ADDING_MEMBER_MESSAGE);
            }
        } else {
            result = buildResult(UNKNOWN_SQUARE_RESULT, UNKNOWN_SQUARE_MESSAGE);
        }

        return result;
    }

    private String processJoinRequest(String[] args, Square square) {
        // command arguments
        // 3 == desired name
        // 4 == member public key
        // 5 == ip address
        // 6 == port

        if (args.length == 7) {
            String file = square.getSafeLowerName() + MEMBER_FILE_EXT;
            String[] sameNames = utility.searchFile(file, args[3], SEARCH_STARTS_WITH);
            String[] sameIds = utility.searchFile(file, args[4], SEARCH_CONTAINS);
            String registeredName = args[3];
            if (sameIds.length < 1) {
                if (sameNames.length > 0) {
                    registeredName += PERCENT + Integer.toString(sameNames.length);
                }

                String data = args[4] + DATA_SEPARATOR + args[5] + DATA_SEPARATOR + args[6];
                LogIt.LogInfo(data);
                return processCommand(registeredName, data, file, square);
            } else {
                return buildResult(ALREADY_REGISTERED_RESULT, ALREADY_REGISTERED_MESSAGE);
            }
        }

        return buildResult(MALFORMED_REQUEST_RESULT, MALFORMED_REQUEST_MESSAGE);
    }

    private String processPostMessage(String[] args, Square square) {
        // command arguments
        // 3 == message to post
        // 4 == member id
        if (args.length == 5) {
            long currentMillis = System.currentTimeMillis();
            String file = square.getSafeLowerName() + POST_FILE_EXT;
            String memberFile = square.getSafeLowerName() + MEMBER_FILE_EXT;
            String[] sameNames = utility.searchFile(memberFile, args[4], SEARCH_CONTAINS);
            if (sameNames.length > 0) {
                return processCommand(Long.toString(currentMillis) + DATA_SEPARATOR + args[3], args[4], file, square);
            } else {
                return buildResult(FORBIDDEN_RESULT, FORBIDDEN_MESSAGE);
            }
        }

        return buildResult(MALFORMED_REQUEST_RESULT, MALFORMED_REQUEST_MESSAGE);
    }

    private String getPosts(Square square, String[] split) {
        // command arguments
        // 3 == last known timestamp
        // 4 == requesting member id

        if (split.length != 5) {
            return EMPTY_STRING;
        }

        String start = split[3];
        String memberId = split[4];

        if (checkSquareAccess(square, memberId)) {
            String file = square.getSafeLowerName() + POST_FILE_EXT;
            String memberFile = square.getSafeLowerName() + MEMBER_FILE_EXT;
            int firstRow = utility.findFirstOccurence(file, start, SEARCH_CONTAINS);
            String posts = utility.readFile(file, firstRow);

            String[] members = utility.readFile(memberFile).split(NEWLINE);

            ArrayList<String> memberIds = new ArrayList<String>();
            ArrayList<String> memberNames = new ArrayList<String>();
            
            for(int x = 0; x < members.length; x++) {
                String[] data = members[x].split(DATA_SEPARATOR);
                memberNames.add(data[0]);
                memberIds.add(data[1]);
            }


            return replaceValues(posts, memberIds.toArray(new String[memberIds.size()]), memberNames.toArray(new String[memberNames.size()]));
        }

        return EMPTY_STRING;
    }

    private String getMembers(Square square, String[] split) {
        // command arguments
        // 3 == member id
        if (split.length != 4) {
            return EMPTY_STRING;
        }

        String memberId = split[3];

        if (checkSquareAccess(square, memberId)) {
            return utility.readFile(square.getSafeLowerName() + MEMBER_FILE_EXT);
        }

        return EMPTY_STRING;
    }

    private String buildResult(String code, String msg) {
        return code + COLON + msg;
    }

    private boolean checkSquareAccess(Square square, String memberId) {
        String file = square.getSafeLowerName() + MEMBER_FILE_EXT;
        int first = utility.findFirstOccurence(file, memberId, SEARCH_CONTAINS);

        return (first > -1);
    }

    private String replaceValues(String source, String[] oldValues, String[] newValues) {
        String result = source;

        if (oldValues.length == newValues.length) {
            for(int x = 0; x < oldValues.length; x++) {
                result = source.replace(oldValues[x], newValues[x]);
            }
        }

        return result;
    }
}

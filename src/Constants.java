import javafx.scene.paint.Color;

public class Constants {
    private Constants() {}

    public static final String VERSION = "0.0.12";
    public static final int BASE_CRYPTO_UTILS = 1;
    public static final int BASE_SQUARE_KEY_PAIR = 1;
    public static final int UTILITY_SQUARE_KEY_PAIR = 2;
    public static final int KEYS_SQUARE_KEY_PAIR = 3;
    public static final int BASE_SQUARE_CONTROLLER = 1;
    public static final int BASE_SERVER = 1;
    public static final int BASE_UTILITY = 1;
    public static final int BASE_SQUARE = 1;
    public static final int BASE_TOWN_SQUARE_BUTTON = 1;
    public static final int BASE_CLIENT = 1;
    public static final int CONSOLE_LOGGER = 1;
    public static final int FILE_LOGGER = 2;
    public static final int ERROR_LOGGER = 3;
    public static final int EMPTY_LOGGER = 9999;
    public static final int BASE_CLIENT_THREAD = 1;
    public static final int BASE_SERVER_THREAD = 1;
    public static final int BASE_TEXT_DIALOG_BOX = 1;
    public static final int BASE_ALERT_BOX = 1;
    public static final int BASE_SYSTEM_EXIT = 1;
    public static final int BASE_VERSION_CHECKER = 1;
    public static final int BASE_MODAL_IMAGE_VIEWER = 1;
    public static final int BASE_MODAL_VIDEO_VIEWER = 2;
    public static final int BASE_MODAL_MEMBER_VIEWER = 3;
    public static final int BASE_SHOW_SQUARE_MEMBERS = 1;
    public static final int BASE_MODAL_LICENSE_VIEWER = 4;
    public static final int HUB_APP = 2;
    public static final int SERVER_DIALOG_CONTROLLER = 2;
    public static final int SYSTEM_EXIT_OK = 0;
    public static final int SYSTEM_EXIT_FAIL = 1;
    public static final int SYSTEM_EXIT_ALREADY_RUNNING = 2;
    public static final int SYSTEM_EXIT_PORT_IN_USE = 3;
    public static final int BASE_COMMAND_CONTROLLER = 1;
    public static final int BASE_MEMBER_POSTS_THREAD = 1;
    public static final int BASE_SYNC_CLONE = 1;
    public static final int NULL_OBJECT_TYPE = Integer.MAX_VALUE;
    public static final String SQUARE_FILE_EXT = ".square";
    public static final String UNIQUE_ID_FILE = "unique.id";
    public static final String DEFAULT_NAME_FILE = "default.name";
    public static final String DEFAULT_SQUARE_FILE = "my_square.square";
    public static final String DEFAULT_SQUARE_ME_FILE = "my_square.members";
    public static final String PORT_FILE = "port.id";
    public static final String IP_FILE = "ip.id";
    public static final String FXML_FILE = "sample.fxml";
    public static final String PUBLIC_KEY_FILE = "public.key";
    public static final String PRIVATE_KEY_FILE = "private.key";
    public static final String LOCK_FILE = "town_square.lock";
    public static final String PAUSE_FILE_EXT = ".pause";
    public static final String LOCK_FILE_CONTENTS = "lock";
    public static final String APP_TITLE = "Town Square";
    public static final String DEFAULT_SQUARE_NAME = "My Square";
    public static final String COMMA = ",";
    public static final String OPEN_PARENS = "(";
    public static final String CLOSE_PARENS = ")";
    public static final String NOT_PRIVATE = "0";
    public static final String DEFAULT_PORT = "44123";
    public static final String EMPTY_STRING = "";
    public static final String NO_PASSWORD_VALUE = "~~~~~~~";
    public static final String DATA_SEPARATOR = "~_~";
    public static final String DEFAULT_IP = "127.0.0.1";
    public static final String POST_BUTTON_TEXT = "Post";
    public static final String TILDE = "~";
    public static final String NEWLINE = "\n";
    public static final String ZERO = "0";
    public static final String TAB_PREFIX = "tab";
    public static final String UNDERSCORE = "_";
    public static final String DASH = "-";
    public static final String SPACE = " ";
    public static final String FOUR_SPACES = "    ";
    public static final String THREE_STRINGS = "   ";
    public static final String PERCENT = "%";
    public static final String HASHTAG = "#";
    public static final String PERIOD_SPLIT = "\\.";
    public static final int JOIN_TYPE = 1;
    public static final int CREATE_TYPE = 2;
    public static final int LINK_URL_TYPE = 3;
    public static final String INVITATION_LABEL_TEXT = "Invitation:";
    public static final String INVITE_CODE_LABEL = "Invite Code:";
    public static final String POSTS_LABEL = "Posts";
    public static final int TEXTFIELD_WIDTH = 400;
    public static final int POSTS_PANE_WIDTH = 565;
    public static final int POSTS_TEXTFIELD_WIDTH = 515;
    public static final double INVITATION_DIALOG_WIDTH = 550;
    public static final String JOIN_SQUARE_TITLE = "Join Square";
    public static final String JOIN_SQUARE_HEADER_TEXT = "Paste the Invitation";
    public static final String CREATE_SQUARE_TITLE = "Create Square";
    public static final String CREATE_SQUARE_HEADER_TEXT = "Name your new Square.";
    public static final String POST_PROMPT_TEXT = "Type your message";
    public static final String JOIN_COMMAND = "join";
    public static final String MEMBER_COMMAND = "members";
    public static final String REQUEST_PUBLIC_KEY_COMMAND = "pkey";
    public static final String REQUEST_FILE_COMMAND = "file";
    public static final String CHECK_VERSION_COMMAND = "ver";
    public static final String OK_RESULT = "200";
    public static final String ALREADY_REGISTERED_RESULT = "460";
    public static final String MEMBERS_FILE_EXT = ".members";
    public static final String POSTS_FILE_EXT = ".posts";
    public static final String MY_SQUARE_DEFAULT = "my_square";
    public static final String FILE_DATA_SEPARATOR = "~_~";
    public static final String USER_DIR = "user.dir";
    public static final String PATH_DELIMITER = "/";
    public static final String REMOTE_IP_URL = "https://childsheartyoga.com/api.aspx?pf=ip"; //"https://checkip.amazonaws.com/"
    public static final int NOT_FOUND_ROW = -1;
    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String COMMAND_DATA_SEPARATOR = "%%%";
    public static final String READ_FILE_DATA_SEPARATOR = "%%%";
    public static final String COLON = ":";
    public static final String QUESTION_MARK = "?";
    public static final String STAR = "*";
    public static final String QUESTION_MARK_SPLIT = "\\?";
    public static final String READ_COMMAND = "read";
    public static final String NO_POSTS = "-1";
    public static final String NO_ROWS = "-1";
    public static final String POST_COMMAND = "post";
    public static final String ACK_COMMAND = "ack";
    public static final boolean NOT_FOUND_RETURN_ZERO = false;
    public static final boolean NOT_FOUND_RETURN_NEG_ONE = true;
    public static final String ACK_BACK = "ack back";
    public static final boolean SEARCH_STARTS_WITH = true;
    public static final boolean SEARCH_CONTAINS = false;
    public static final String FAILURE_COMMAND = "failure";
    public static final String INTERNAL_ERROR_RESULT = "500";
    public static final String UNKNOWN_REQUEST = "400";
    public static final String MALFORMED_REQUEST_RESULT = "401";
    public static final String UNKNOWN_SQUARE_RESULT = "402";
    public static final String FORBIDDEN_RESULT = "403";
    public static final String DECRYPTION_FAILURE_RESULT = "450";
    public static final String UNKNOWN_COMMAND_RESULT = "499";
    public static final String UNKNOWN_REQUEST_MESSAGE = "unknown request";
    public static final String DECRYPTION_FAILURE_MESSAGE = "invalid password";
    public static final String MALFORMED_REQUEST_MESSAGE = "malformed request";
    public static final String ADDED_MESSAGE = "added";
    public static final String ADDING_MEMBER_MESSAGE = "adding member";
    public static final String UNKNOWN_SQUARE_MESSAGE = "unknown square";
    public static final String FORBIDDEN_MESSAGE = "forbidden";
    public static final String ALREADY_REGISTERED_MESSAGE = "already registered";
    public static final String ENCRYPTION_SCHEME = "RSA/ECB/PKCS1Padding";
    public static final String RSA = "RSA";
    public static final String DEFAULT_USER_NAME = "Anonymous User";
    public static final String SERVER_LOG_FILE = "_server.log";
    public static final String MAIN_LOG_FILE = "_main.log";
    public static final String MAIN = "_main";
    public static final String LOG_FILE_EXT = ".log";
    public static final String CLIENT_LOG_PREFIX = "_client_";
    public static final String INVITE_FILE_EXT = ".invite";
    public static final String INVITE_FILE_PREFIX = "__pending_";
    public static final int GRACEFUL_SHUTDOWN = 0;
    public static final String SQUARE_CONTROLLER_LOG_FILE = "squareController.log";
    public static final String IMAGE_MARKER = "[image]";
    public static final String END_SQUARE_BRACKET = "]";
    public static final String VIDEO_MARKER = "[video]";
    public static final String FILE_MARKER = "[file]";
    public static final String IMAGE_DIALOG_TITLE = "Attach Image...";
    public static final String VIDEO_DIALOG_TITLE = "Attach Video...";
    public static final String FILE_DIALOG_TITLE = "Attach File...";
    public static final int NOT_FOUND_IN_STRING = -1;
    public static final String JAR_FILE = "App.jar";
    public static final String TEMP_JAR_FILE = "_new_App.jar";
    public static final String FORWARD_SLASH = "/";
    public static final double MIN_WINDOW_WIDTH = 614.4000244140625;
    public static final double MIN_WINDOW_HEIGHT = 463.20001220703125;
    public static final double TAB_PANE_HEIGHT_DIFF = 63.20001220703125;
    public static final double TAB_PANE_WIDTH_DIFF = 14.4000244140625;
    public static final double POSTS_BOX_WIDTH_DIFF = 50;
    public static final double POSTS_TEXT_FIELD_WIDTH_DIFF = 99;
    public static final String DEFAULT_SQUARE_TAB_NAME = "tabDefaultSquare";
    public static final int IMAGE_SMALL_FIT_HEIGHT = 100;
    public static final int IMAGE_SMALL_FIT_WIDTH = 100;
    public static final int IMAGE_LARGE_FIT_HEIGHT = 500;
    public static final int IMAGE_LARGE_FIT_WIDTH = 400;
    public static final int MAX_MESSAGES = 100;
    public static final String EXIT_SQUARE_TEXT = "exit";
    public static final String PAUSE_FILE_CONTENTS = "pause";
    public static final String LEAVE_SQUARE_TITLE = "Leave Square?";
    public static final String LEAVE_SQUARE_HEADER = "Are you sure you want to leave?";
    public static final String LEAVE_SQUARE_CONTENT = "You can rejoin the square using a valid invitation.";
    public static final String NULL_TEXT = "null";
    public static final String LEAVE_FILE_EXT = ".leave";
    public static final String LEAVE_FILE_CONTENTS = "leave";
    public static final String COMMAND_PREFIX = "/";
    public static final String UNBLOCK_COMMAND = "unblock";
    public static final String BLOCK_COMMAND = "block";
    public static final String BLOCK_FILE_EXT = ".block";
    public static final String ABOUT_COMMAND = "about";
    public static final String HELP_COMMAND = "help";
    public static final String HIDE_COMMAND = "hide";
    public static final String EXPOSE_COMMAND = "expose";
    public static final String PAUSE_COMMAND = "pause";
    public static final String UNPAUSE_COMMAND = "unpause";
    public static final String NICKNAME_COMMAND = "nickname";
    public static final String VERSION_COMMAND = "ver";
    public static final String ADD_MEMBER_COMMAND = "addmember";
    public static final String SELF_COMMAND = "self";
    public static final String COMMANDS_TITLE = "List of Commands";
    public static final String SEMI_COLON = ";";
    public static final int PAUSE_WAIT_TIME = 10000;
    public static final String WAIT_TIME_FILE = "wait.time";
    public static final int DEFAULT_WAIT_TIME = 1000;
    public static final int RESIZE_WAIT_TIME = 210;
    public static final String HELLO_WORLD = "hello world";
    public static final int ASCII_ZERO = 48;
    public static final int ASCII_LOWER_Z = 122;
    public static final String ABOUT_TITLE = "About Town Square";
    public static final String ABOUT_HEADER = "Town Square Messaging App";
    public static final String VERSION_TEXT_PREFIX = "Version: ";
    public static final String COMMANDS_HEADER = "All Commands start with a '/' (forward slash)";
    public static final String PAUSED_TAB_NOTIFICATION = " (paused)";
    public static final String CLOSING_LOG_MESSAGE = "Closing";
    public static final int NO_BUTTON = -1;
    public static final int PRIMARY_BUTTON = 1;
    public static final int SECONDARY_BUTTON = 2;
    public static final int EQUALS_VALUE = 0;
    public static final String BLOCKED_IMAGE_FILE = "blocked-image.png";
    public static final String GO_AWAY = "go away";
    public static final String KEY_FILE_EXT = ".key";
    public static final String BLOCKED_VIDEO_FILE = "blocked-video.mp4";
    public static final String CSS_STYLE_HAND = "-fx-cursor: hand;";
    public static final String UNENCRYPTED_FLAG = "u";
    public static final String ENCRYPTED_FLAG = "e";
    public static final boolean DO_NOT_ENCRYPT_CLIENT_TRANSFER = false;
    public static final boolean ENCRYPT_CLIENT_TRANSFER = true;
    public static final int TINY_PAUSE = 10;
    public static final int INFINITE_LOOP_FLAG = -1;
    public static final int REALLY_LOW_NUMBER = -999;
    public static final String SERVER_HIDING_TITLE = " (hiding)";
    public static final String ID_FILE_EXT = ".id";
    public static final String JAR_FILE_EXT = ".jar";
    public static final String TXT_FILE_EXT = ".txt";
    public static final String SH_FILE_EXT = ".sh";
    public static final String BAT_FILE_EXT = ".bat";
    public static final String ZIP_FILE_EXT = ".zip";
    public static final String URL_MARKER = "[url]";
    public static final Color LINK_COLOR = Color.color(0.0, 0.29, 0.6);
    public static final String DEVELOPER_ONE_NAME = "oak-and-lion";
    public static final String DEVELOPED_BY = "Developed by: ";
    public static final String GITHUB_REPO = "https://github.com/oak-and-lion/town-square";
    public static final String URL_HEADER = "URL Address";
    public static final String URL_CONTENT = "Enter URL Address";
    public static final double DEFAULT_WIDTH = -1;
    public static final double DEFAULT_HEIGHT = -1;
    public static final String LICENSE_COMMAND = "license";
    public static final String CLONE_COMMAND = "clone";
    public static final String DNA_COMMAND = "dna";
    public static final String CLONE_CHALLENGE = "clone dna unlock";
    public static final String DNA_FILE_EXT = ".dna";
    public static final String CLONE_FILE_EXT = ".clone";
    public static final String TEMP_FILE_EXT = ".temp";
    public static final String SEND_CLONE_COMMAND = "getclone";
    public static final String TEMP_ZIP_FILE = "temp.zip";
    public static final int ENCRYPTION_KEY_LENGTH = 16;
    public static final String CLONE_MESSAGE_TITLE = "Clone Information Retrieved";
    public static final String CLONE_MESSAGE_HEADER = "Clone Information Needs to be Applied";
    public static final String CLONE_MESSAGE = "You must restart Town Square in order to apply\nthe Clone Information. After you have exited Town Square,\nyou can either unzip the 'my_square.clone' file\nor use the start script packaged to apply it automatically.";
    public static final String GET_APP_JAR_COMMAND = "appfile";
    public static final String NEW_APP_VER_FILE = "_new_App.ver";
    public static final String MEMBER_BUTTON_TEXT = "Members";
    public static final String NICKNAME_FILE_EXT = ".nickname";
    public static final String SINGLE_QUOTE = "'";
    public static final String DELETE_COMMAND = "delete";
    public static final String HUB_REGISTRATION_FILE = "registration.hub";
    public static final String REGISTER_HUB = "reghub";
    public static final String SEND_MESSAGE = "send";
    public static final String ERROR_LOG_FILE = "_error.log";
    public static final String SYNC_CLONE = "syncclone";
    public static final int SYNC_CLONE_WAIT = 10000;
}
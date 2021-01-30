public interface ICommandController {
    Boolean[] processCommand(String command, ISquare square);
    boolean blockUser(String user, ISquare square);
}

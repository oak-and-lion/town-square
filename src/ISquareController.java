public interface ISquareController {
    SquareResponse processRequest(String request, RequesterInfo requester);
    boolean isHiding();
    ILogIt getLogger();
}

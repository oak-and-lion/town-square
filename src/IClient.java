public interface IClient {
    String sendMessage(String text, boolean encrypt) ;
    String getSquareId();
}

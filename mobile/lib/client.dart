import 'dart:convert';
import 'dart:io';
import 'client_message_package.dart';
import 'constants.dart';
import 'iclient.dart';

class Client implements IClient {
  String _ip;
  int _port;

  Client(this._ip, this._port);

  Future<void> sendMessage(ClientMessagePackage package) async {
    Socket socket = await Socket.connect(_ip, _port);

    String result = Constants.EMPTY_STRING;
    // listen to the received data event stream
    socket.listen((List<int> event) {
      result = utf8.decode(event);
      if (result != Constants.TERMINATION_CODE) {
        package.setResult(result);
        // callback to the original requestor with the updated response
        package.getCallback()(package);
      }
    });

    // send message
    socket.add(utf8.encode(package.getMessage()));

    // .. and close the socket
    socket.close();
  }
}

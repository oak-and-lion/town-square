import 'iclient.dart';

class ClientMessagePackage {
  IClient _client;
  Function(ClientMessagePackage) _func;
  String _message;
  String _userData;
  String _result;
  String _clientPubKey;

  ClientMessagePackage(this._client, this._func, this._message, this._userData,
      this._result, this._clientPubKey);

  IClient getClient() {
    return _client;
  }

  Function(ClientMessagePackage) getCallback() {
    return _func;
  }

  void setCallback(Function(ClientMessagePackage) value) {
    _func = value;
  }

  void setMessage(String value) {
    _message = value;
  }

  String getMessage() {
    return _message;
  }

  void setUserData(String value) {
    _userData = value;
  }

  String getUserData() {
    return _userData;
  }

  void setResult(String value) {
    _result = value;
  }

  String getResult() {
    return _result;
  }

  void setClientPubKey(String value) {
    _clientPubKey = value;
  }

  String getClientPubKey() {
    return _clientPubKey;
  }
}

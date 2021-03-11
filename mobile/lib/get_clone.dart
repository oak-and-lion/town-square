import 'dart:typed_data';
import 'client_message_package.dart';
import 'constants.dart';
import 'double_string.dart';
import 'iclient.dart';
import 'iget_clone.dart';
import 'iapp.dart';
import 'ifactory.dart';
import 'iutility.dart';
import 'square_response.dart';

class GetClone implements IGetClone {
  IFactory _factory;
  IApp _app;
  IUtility _utility;

  GetClone(this._factory, this._utility, this._app);

  Future<void> getClone(String cloneInfo) async {
    List<String> args = cloneInfo.split(Constants.SPACE);
    String hostName = args[0];
    int port = int.parse(args[1]);
    String squareId = args[2];

    IClient client =
        _factory.createClient(Constants.BASE_CLIENT_TYPE, hostName, port);

    ClientMessagePackage package = ClientMessagePackage(
        client,
        processGetCloneCallback,
        Constants.UNENCRYPTED_FLAG +
            Constants.COMMAND_SEPERATOR +
            squareId +
            Constants.COMMAND_SEPERATOR +
            Constants.PUBLIC_KEY_COMMAND,
        cloneInfo,
        Constants.EMPTY_STRING,
        Constants.EMPTY_STRING);
    await client.sendMessage(package);
  }

  void processGetCloneCallback(ClientMessagePackage package) async {
    SquareResponse response = getResponse(package.getResult());
    if (response.getCode() == Constants.OK_CODE ||
        response.getCode() == Constants.ALREADY_REGISTERED_CODE) {
      package.setClientPubKey(response.getData());
      await processClone(package);
    }
  }

  Future<void> processClone(ClientMessagePackage package) async {
    List<String> cloneRequest = package.getUserData().split(Constants.SPACE);
    String clonePassword = cloneRequest[3];
    DoubleString passwordInfo = _utility.createPassword(
        Constants.PASSWORD_LENGTH, package.getClientPubKey());

    String encrypted = _utility.encryptData(
        Constants.CLONE_COMMAND +
            Constants.COMMAND_SEPERATOR +
            clonePassword +
            Constants.COMMAND_SEPERATOR +
            Constants.DUMMY_IP_ADDRESS +
            Constants.COMMAND_SEPERATOR +
            Constants.DUMMY_PORT,
        passwordInfo.getStringOne());

    String message = Constants.ENCRYPTED_FLAG +
        Constants.COMMAND_SEPERATOR +
        cloneRequest[2] +
        Constants.COMMAND_SEPERATOR +
        passwordInfo.getStringTwo() +
        Constants.COMMAND_SEPERATOR +
        encrypted;

    package.setMessage(message);
    package.setCallback(processCloneCallback);
    await package.getClient().sendMessage(package);
  }

  Future<void> processCloneCallback(ClientMessagePackage package) async {
    List<String> cloneRequest = package.getUserData().split(Constants.SPACE);
    String tempPass = cloneRequest[3];
    for (int x = tempPass.length; x < Constants.CLONE_KEY_LENGTH; x++) {
      tempPass += Constants.UNDERSCORE;
    }
    SquareResponse response = getResponse(package.getResult());
    if (response.getCode() == Constants.OK_CODE) {
      String b64 = _utility.decryptData(response.getData(),
          _utility.base64Encode(Uint8List.fromList(tempPass.codeUnits)));
      Uint8List data = _utility.base64Decode(b64);
      bool result = _utility.writeBinaryFile(
          Constants.MY_SQUARE_NAME + Constants.CLONE_FILE_EXT, data);
      if (result) {
        _utility.unzip(Constants.MY_SQUARE_NAME + Constants.CLONE_FILE_EXT);
        _app.sendMessage("Clone successful");
      }
    }
  }

  SquareResponse getResponse(String data) {
    String temp = data
        .replaceAll(Constants.TERMINATION_CODE, Constants.EMPTY_STRING)
        .replaceAll(Constants.NEWLINE, Constants.EMPTY_STRING)
        .replaceAll(Constants.CARRIAGE_RETURN, Constants.EMPTY_STRING);
    List<String> result = temp.split(Constants.COLON);
    String code = Constants.INVALID_CODE;
    String message = Constants.EMPTY_STRING;

    if (result.length > 0) {
      code = result[0];
    }
    if (result.length > 1) {
      message = result[1];
    }
    return SquareResponse(code, message, temp);
  }
}

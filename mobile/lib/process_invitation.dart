import 'iprocess_invitation.dart';
import 'client_message_package.dart';
import 'constants.dart';
import 'double_string.dart';
import 'iapp.dart';
import 'iclient.dart';
import 'ifactory.dart';
import 'iutility.dart';
import 'square_response.dart';

class ProcessInvitation implements IProcessInvitation {
  IFactory _factory;
  IUtility _utility;
  IApp _app;
  ProcessInvitation(this._factory, this._utility, this._app);

  Future<void> processInvitation(String invitation) async {
    List<String> invite = invitation.split(Constants.TILDE);
    IClient client = _factory.createClient(
        Constants.BASE_CLIENT_TYPE, invite[1], int.parse(invite[2]));
    ClientMessagePackage package = ClientMessagePackage(
        client,
        processInviteCallback,
        Constants.UNENCRYPTED_FLAG +
            Constants.COMMAND_SEPERATOR +
            invite[3] +
            Constants.COMMAND_SEPERATOR +
            Constants.PUBLIC_KEY_COMMAND,
        invitation,
        Constants.EMPTY_STRING,
        Constants.EMPTY_STRING);
    await client.sendMessage(package);
  }

  Future<void> processInviteCallback(ClientMessagePackage package) async {
    SquareResponse response = getResponse(package.getResult());
    if (response.getCode() == Constants.OK_CODE ||
        response.getCode() == Constants.ALREADY_REGISTERED_CODE) {
      package.setClientPubKey(response.getData());
      await processJoin(package);
    }
  }

  Future<void> processJoin(ClientMessagePackage package) async {
    List<String> invite = package.getUserData().split(Constants.TILDE);
    DoubleString passwordInfo = _utility.createPassword(
        Constants.PASSWORD_LENGTH, package.getClientPubKey());

    String encrypted = _utility.encryptData(
        Constants.JOIN_COMMAND +
            Constants.COMMAND_SEPERATOR +
            _utility.readFile(Constants.NAME_FILE) +
            Constants.COMMAND_SEPERATOR +
            _utility.readFile(Constants.PUBLIC_KEY_FILE) +
            Constants.COMMAND_SEPERATOR +
            Constants.DUMMY_IP_ADDRESS +
            Constants.COMMAND_SEPERATOR +
            Constants.DUMMY_PORT +
            Constants.COMMAND_SEPERATOR +
            _utility.readFile(Constants.UNIQUE_ID_FILE),
        passwordInfo.getStringOne());

    String message = Constants.ENCRYPTED_FLAG +
        Constants.COMMAND_SEPERATOR +
        invite[3] +
        Constants.COMMAND_SEPERATOR +
        passwordInfo.getStringTwo() +
        Constants.COMMAND_SEPERATOR +
        encrypted;
    package.setMessage(message);
    package.setCallback(processJoinCallback);
    await package.getClient().sendMessage(package);
  }

  Future<void> processMemberCallback(ClientMessagePackage package) async {
    SquareResponse response = getResponse(package.getResult());
    bool result = _utility.writeFile(
        package.getUserData() + Constants.MEMBERS_FILE_EXT,
        response
            .getSplit()[1]
            .replaceAll(Constants.COMMAND_SEPERATOR, Constants.NEWLINE));
    if (result) {
      _app.sendMessage("Square Joined");
    }
  }

  void processJoinCallback(ClientMessagePackage package) async {
    SquareResponse response = getResponse(package.getResult());
    List<String> invite = package.getUserData().split(Constants.TILDE);
    if (response.getCode() == Constants.OK_CODE ||
        response.getCode() == Constants.ALREADY_REGISTERED_CODE) {
      List<String> responseData = response.getSplit();
      String squareSafeName = _utility.safeString(responseData[3]);
      _utility.deleteFile(squareSafeName + Constants.PAUSE_FILE_EXT);
      _utility.deleteFile(squareSafeName + Constants.LEAVE_FILE_EXT);
      _utility.writeFile(
          squareSafeName + Constants.SQUARE_FILE_EXT,
          responseData[3] +
              Constants.COMMA +
              invite[3] +
              Constants.COMMA +
              Constants.TAB_PREFIX +
              squareSafeName +
              Constants.COMMA +
              Constants.ZERO +
              Constants.COMMA +
              Constants.NO_PASSWORD);

      DoubleString passwordInfo = _utility.createPassword(
          Constants.PASSWORD_LENGTH, package.getClientPubKey());
      String encrypted = _utility.encryptData(
          Constants.MEMBER_COMMAND +
              Constants.COMMAND_SEPERATOR +
              _utility.readFile(Constants.UNIQUE_ID_FILE),
          passwordInfo.getStringOne());
      String message = Constants.ENCRYPTED_FLAG +
          Constants.COMMAND_SEPERATOR +
          invite[3] +
          Constants.COMMAND_SEPERATOR +
          passwordInfo.getStringTwo() +
          Constants.COMMAND_SEPERATOR +
          encrypted;
      package.setUserData(squareSafeName);
      package.setMessage(message);
      package.setCallback(processMemberCallback);
      await package.getClient().sendMessage(package);
    }
  }

  SquareResponse getResponse(String data) {
    String temp = data
        .replaceAll(Constants.TERMINATION_CODE, Constants.EMPTY_STRING)
        .replaceAll("\n", Constants.EMPTY_STRING)
        .replaceAll("\r", Constants.EMPTY_STRING);
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

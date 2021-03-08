import 'dart:convert';

import 'package:steel_crypt/steel_crypt.dart';
import 'client_message_package.dart';
import 'square_response.dart';
import 'constants.dart';
import 'ifactory.dart';
import 'isquare.dart';
import 'iutility.dart';
import 'iview.dart';
import 'iapp.dart';
import 'rsa_pem.dart';
import 'iclient.dart';

class App implements IApp {
  IView _view;
  IUtility _utility;
  RsaKeyHelper _helper;
  IFactory _factory;
  List<ISquare> _allSquares;

  App(this._utility, this._view, this._helper, this._factory, this._allSquares);

  void initialize() async {
    if (!_utility.checkFileExists(Constants.UNIQUE_ID_FILE)) {
      _utility.writeFile(Constants.UNIQUE_ID_FILE, _utility.createGUID());
    }
    if (!_utility.checkFileExists(Constants.PUBLIC_KEY_FILE)) {
      var keyPair = _helper.generateKeyPair();
      _utility.writeFile(Constants.PRIVATE_KEY_FILE,
          _helper.encodePrivateKeyToPem(keyPair.privateKey));
      _utility.writeFile(Constants.PUBLIC_KEY_FILE,
          _helper.encodePublicKeyToPem(keyPair.publicKey));
    }

    var pk = _utility.readFile(Constants.PUBLIC_KEY_FILE);
    _helper.parsePublicKeyFromPem(pk);
    String test =
        _helper.encrypt("_thisisapassword", _helper.parsePublicKeyFromPem(pk));
    sendMessage(test);
    pk = _utility.readFile(Constants.PRIVATE_KEY_FILE);
    _helper.parsePrivateKeyFromPem(pk);
    String dtest = _helper.decrypt(test, _helper.parsePrivateKeyFromPem(pk));
    sendMessage(dtest);
    buildSquares();
  }

  void buildSquares() {
    List<String> squares = _utility.getFiles(Constants.SQUARE_FILE_EXT);

    for (var square in squares) {
      String squareInfo = _utility.readFile(square);
      ISquare newSquare =
          _factory.createSquare(Constants.BASE_SQUARE_TYPE, squareInfo, this);
      _allSquares.add(newSquare);
    }
  }

  void start() {
    initialize();
    _view.sendMessage("start complete");
  }

  void processInvitation(String invitation) async {
    List<String> invite = invitation.split(Constants.TILDE);
    IClient client = _factory.createClient(
        Constants.BASE_CLIENT_TYPE, invite[1], int.parse(invite[2]));
    ClientMessagePackage package = ClientMessagePackage(
        client,
        processInviteCallback,
        "u%%%" + invite[3] + "%%%pkey",
        invitation,
        Constants.EMPTY_STRING,
        Constants.EMPTY_STRING);
    client.sendMessage(package);
  }

  void processInviteCallback(ClientMessagePackage package) {
    SquareResponse response = getResponse(package.getResult());
    if (response.getCode() == Constants.OK_CODE ||
        response.getCode() == Constants.ALREADY_REGISTERED_CODE) {
      package.setClientPubKey(response.getData());
      processJoin(package);
    }
  }

  void processJoin(ClientMessagePackage package) {
    sendMessage(package.getResult());
    SquareResponse response = getResponse(package
        .getResult()
        .replaceAll("200:terminated", Constants.EMPTY_STRING));
    sendMessage(response.getData());
    List<String> invite = package.getUserData().split(Constants.TILDE);
    String password = "_thisisapassword";
    RsaKeyHelper tempHelper = RsaKeyHelper();
    String encryptedPassword = tempHelper.encrypt(
        password, tempHelper.parsePublicKeyFromPem(response.getData()));

    String encrypted = encryptData(
        "%%%join%%%dart test%%%" +
            _utility.readFile(Constants.PUBLIC_KEY_FILE) +
            "%%%1.1.1.1%%%1%%%" +
            _utility.readFile(Constants.UNIQUE_ID_FILE),
        password);
    String message = "e%%%" +
        invite[3] +
        Constants.COMMAND_SEPERATOR +
        encryptedPassword +
        Constants.COMMAND_SEPERATOR +
        encrypted;
    package.setMessage(message);
    sendMessage(message);
    package.setCallback(processJoinCallback);
    package.getClient().sendMessage(package);
  }

  void processJoinCallback(ClientMessagePackage package) {
    SquareResponse response = getResponse(package.getResult());
    sendMessage(package.getUserData());
    sendMessage(response.getCode() + " " + response.getData());
  }

  SquareResponse getResponse(String data) {
    List<String> result = data.split(Constants.COLON);
    String code = Constants.INVALID_CODE;
    String message = Constants.EMPTY_STRING;

    if (result.length > 0) {
      code = result[0];
    }
    if (result.length > 1) {
      message = result[1];
    }
    return SquareResponse(code, message);
  }

  void setName(String value) {}

  void sendMessage(String msg) {
    _view.sendMessage(msg);
  }

  void noSquares() {}

  String encryptData(String data, String password) {
    var fortunaKey = CryptKey()
        .genFortuna(); //generate 32 byte key with Fortuna; you can also enter your own
    /*var nonce = CryptKey().genDart(
        len:
            12); //generate IV for AES with Dart Random.secure(); you can also enter your own*/
    var aesEncrypter = AesCrypt(
        key: fortunaKey,
        padding: PaddingAES
            .pkcs7); //generate AES encrypter with key and PKCS7 padding
    String encrypted =
        aesEncrypter.gcm.encrypt(inp: data, iv: password); //encrypt using GCM
    String decrypted =
        aesEncrypter.gcm.decrypt(enc: encrypted, iv: password); //decrypt
    sendMessage(decrypted);
    return encrypted;
  }
}

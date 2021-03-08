import 'process_invitation.dart';
import 'constants.dart';
import 'ifactory.dart';
import 'isquare.dart';
import 'iutility.dart';
import 'iview.dart';
import 'iapp.dart';
import 'rsa_pem.dart';

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

  void registerHub(String hubInfo, String hubName) {
    _utility.writeFile(hubName + Constants.HUB_REGISTRATION_FILE_EXT, hubInfo);
  }

  void processInvitation(String invitation) async {
    ProcessInvitation processInvitation =
        ProcessInvitation(_factory, _utility, this);
    await processInvitation.processInvitation(invitation);
  }

  void setName(String value) {}

  void sendMessage(String msg) {
    _view.sendMessage(msg);
  }

  void noSquares() {}
}

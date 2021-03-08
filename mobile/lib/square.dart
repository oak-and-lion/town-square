import 'iapp.dart';
import 'isquare.dart';

class Square implements ISquare {
  String name;
  String invite;
  String tabName;
  String private;
  String password;
  IApp _app;

  Square(this.name, this.invite, this.tabName, this.private, this.password,
      this._app) {
    _app.sendMessage("new square created");
  }
}

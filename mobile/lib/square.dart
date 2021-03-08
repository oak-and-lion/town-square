import 'iapp.dart';
import 'ifactory.dart';
import 'isquare.dart';

class Square implements ISquare {
  String name;
  String invite;
  String tabName;
  String private;
  String password;
  IFactory _factory;
  IApp _app;

  Square(this.name, this.invite, this.tabName, this.private, this.password,
      this._app, this._factory) {
    _app.sendMessage("new square created");
  }
}

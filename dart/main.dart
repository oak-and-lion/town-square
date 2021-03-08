import 'dart:convert';
import 'dart:io';
import '../mobile/lib/utililty.dart';
import '../mobile/lib/iutility.dart';
import '../mobile/lib/iview.dart';
import '../mobile/lib/iapp.dart';
import '../mobile/lib/app.dart';
import '../mobile/lib/rsa_pem.dart';
import '../mobile/lib/ifactory.dart';
import '../mobile/lib/factory.dart';
import 'storage.dart';

void main() async {
  print("hello, world");

  IUtility _utility = new Utility(new ConsoleStorage(), "./");
  IView consoleView = new ConsoleView();
  IFactory fact = new TownSquareFactory();
  IApp _app = new App(_utility, consoleView, new RsaKeyHelper(), fact, []);

  await _utility
      .init(() => {consoleView.sendMessage("starting app..."), _app.start()});
  consoleView.sendMessage("Enter invitation: ");
  var line = stdin.readLineSync(encoding: Encoding.getByName('utf-8'));
  _app.processInvitation(line);
}

class ConsoleView implements IView {
  void sendMessage(String msg) {
    print(msg);
  }
}

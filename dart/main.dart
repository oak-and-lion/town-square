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
import '../mobile/lib/constants.dart';
import 'storage.dart';

void main() async {
  print("hello, world");

  IUtility _utility = new Utility(new ConsoleStorage(), "./");
  IView consoleView = new ConsoleView();
  IFactory fact = new TownSquareFactory();
  IApp _app = new App(_utility, consoleView, new RsaKeyHelper(), fact, []);

  await _utility
      .init(() => {consoleView.sendMessage("starting app..."), _app.start()});
  String line = Constants.EMPTY_STRING;
  while (line != 'done') {
    consoleView.sendMessage("Enter command: ");
    line = stdin.readLineSync(encoding: Encoding.getByName('utf-8'));
    if (line.startsWith("invite")) {
      await _app.processInvitation(
          line.replaceAll("invite", Constants.EMPTY_STRING).trim());
    } else if (line.startsWith("reghub")) {
      List<String> split = line
          .replaceAll("reghub", Constants.EMPTY_STRING)
          .trim()
          .split(Constants.SPACE);
      _app.registerHub(split[0], split[1]);
    }
  }
}

class ConsoleView implements IView {
  void sendMessage(String msg) {
    print(msg);
  }
}

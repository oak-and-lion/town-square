import 'constants.dart';

class IUtility {
  Future<bool> init(Function() f) async {
    return false;
  }

  bool checkFileExists(String file) {
    return false;
  }

  bool writeFile(String fileName, String data) {
    return false;
  }

  String readFile(String fileName) {
    return Constants.emptyString;
  }

  String createGUID() {
    return Constants.emptyString;
  }
}

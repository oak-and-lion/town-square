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
    return Constants.EMPTY_STRING;
  }

  bool deleteFile(String fileName) {
    return false;
  }

  String createGUID() {
    return Constants.EMPTY_STRING;
  }

  List<String> getFiles(String pattern) {
    return [];
  }
}

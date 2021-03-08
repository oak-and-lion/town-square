import 'dart:convert';
import 'dart:typed_data';

import 'constants.dart';
import 'double_string.dart';

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

  String getRandomString(int length) {
    return "123456";
  }

  String base64Encode(Uint8List data) {
    return base64.encode(data);
  }

  Uint8List base64Decode(String data) {
    return base64.decode(data);
  }

  String encryptData(String data, String password) {
    return Constants.EMPTY_STRING;
  }

  String decryptData(String data, String password) {
    return Constants.EMPTY_STRING;
  }

  String safeString(String s) {
    return s.replaceAll(Constants.SPACE, Constants.UNDERSCORE).toLowerCase();
  }

  DoubleString createPassword(int len, String publicKey) {
    return DoubleString(Constants.EMPTY_STRING, Constants.EMPTY_STRING);
  }
}

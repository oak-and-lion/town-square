import 'iutility.dart';
import 'package:uuid/uuid.dart';
import 'dart:io' as io;
import 'constants.dart';
import 'package:path_provider/path_provider.dart';

class Utility implements IUtility {
  io.Directory _directory;

  Utility() {
    _directory = null;
  }

  Future<bool> init() async {
    _directory = await getApplicationDocumentsDirectory();

    return true;
  }

  String convertPath(String path) {
    String p = "";
    if (_directory != null) {
      p = _directory.path;
    }
    p += path;
    return p;
  }

  bool checkFileExists(String file) {
    var syncPath = convertPath(file);

    return io.File(syncPath).existsSync();
  }

  String readFile(String fileName) {
    var syncPath = convertPath(fileName);

    var exists = io.File(syncPath).existsSync();

    if (exists) {
      return io.File(syncPath).readAsStringSync();
    }

    return Constants.emptyString;
  }

  bool writeFile(String fileName, String data) {
    var syncPath = convertPath(fileName);

    var exists = io.File(syncPath).existsSync();

    if (exists) {
      try {
        io.File(syncPath).deleteSync();
      } catch (fd) {}
    }

    try {
      io.File(syncPath).writeAsStringSync(data);
    } catch (fse) {
      return false;
    }

    return true;
  }

  String createGUID() {
    var uuid = Uuid();

    return uuid.v1();
  }
}

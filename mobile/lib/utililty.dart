import 'istorage.dart';
import 'iutility.dart';
import 'dart:io' as io;
import 'constants.dart';

class Utility implements IUtility {
  String _directory;
  IStorage _storage;

  Utility(this._storage, this._directory);

  Future<bool> init(Function() callback) async {
    _directory = await _storage.getPathToDownload();

    callback();
    return true;
  }

  String convertPath(String path) {
    String p = Constants.EMPTY_STRING;
    if (_directory != null) {
      if (!_directory.endsWith("/")) {
        _directory += "/";
      }
      p = _directory;
    }
    p += path;
    return p;
  }

  List<String> getFiles(String pattern) {
    io.Directory(convertPath(Constants.EMPTY_STRING)).listSync();
    return [];
  }

  bool checkFileExists(String file) {
    var syncPath = convertPath(file);

    return io.File(syncPath).existsSync();
  }

  String readFile(String fileName) {
    var syncPath = convertPath(fileName);

    var exists = checkFileExists(fileName);

    if (exists) {
      return io.File(syncPath).readAsStringSync();
    }

    return Constants.EMPTY_FILE;
  }

  bool writeFile(String fileName, String data) {
    var syncPath = convertPath(fileName);

    deleteFile(fileName);

    try {
      io.File(syncPath).writeAsStringSync(data);
    } catch (fse) {
      return false;
    }

    return true;
  }

  bool deleteFile(String fileName) {
    var syncPath = convertPath(fileName);

    var exists = io.File(syncPath).existsSync();

    if (exists) {
      try {
        io.File(syncPath).deleteSync();
      } catch (fd) {
        return false;
      }
    }

    return true;
  }

  String createGUID() {
    return '12345';
  }
}

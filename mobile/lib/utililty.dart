import 'dart:io';

import 'package:ext_storage/ext_storage.dart';

import 'iutility.dart';
import 'package:uuid/uuid.dart';
import 'dart:io' as io;
import 'constants.dart';
import 'package:path_provider/path_provider.dart';

class Utility implements IUtility {
  String _directory;

  Utility() {
    _directory = null;
  }

  Future<bool> init(Function() callback) async {
    _directory = await _getPathToDownload();

    callback();
    return true;
  }

  Future<String> _getPathToDownload() async {
    if (Platform.isAndroid) {
      return await ExtStorage.getExternalStoragePublicDirectory(
          ExtStorage.DIRECTORY_DOWNLOADS); // /storage/emulated/0/Download
    } else if (Platform.isIOS) {
      return (await getApplicationDocumentsDirectory()).path;
    }
    return (await getApplicationDocumentsDirectory()).path;
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

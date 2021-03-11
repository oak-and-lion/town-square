import 'dart:convert';
import 'dart:math';
import 'dart:typed_data';

import 'package:archive/archive.dart';
import 'package:steel_crypt/steel_crypt.dart';
import 'package:uuid/uuid.dart';

import 'double_string.dart';
import 'istorage.dart';
import 'iutility.dart';
import 'dart:io' as io;
import 'constants.dart';
import 'rsa_pem.dart';

class Utility implements IUtility {
  String _directory;
  IStorage _storage;
  Random _rnd = Random();

  Utility(this._storage, this._directory);

  Future<bool> init(Function() callback) async {
    _directory = await _storage.getPathToDownload();

    callback();
    return true;
  }

  String convertPath(String path) {
    String p = Constants.EMPTY_STRING;
    if (_directory != null) {
      if (!_directory.endsWith(Constants.FORWARD_SLASH)) {
        _directory += Constants.FORWARD_SLASH;
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

  bool writeBinaryFile(String fileName, Uint8List data) {
    var syncPath = convertPath(fileName);

    deleteFile(fileName);

    try {
      io.File(syncPath).writeAsBytesSync(data);
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
    var uuid = Uuid();
    return uuid.v4();
  }

  String getRandomString(int length) {
    return String.fromCharCodes(Iterable.generate(
        length,
        (_) => Constants.CHARACTERS
            .codeUnitAt(_rnd.nextInt(Constants.CHARACTERS.length))));
  }

  String base64Encode(Uint8List data) {
    return base64.encode(data);
  }

  Uint8List base64Decode(String data) {
    return base64.decode(data);
  }

  String encryptData(String data, String password) {
    var aesEncrypter = AesCrypt(
        key: base64Encode(Uint8List.fromList(password.codeUnits)),
        padding: PaddingAES
            .pkcs7); //generate AES encrypter with key and PKCS7 padding
    String encrypted = aesEncrypter.ecb.encrypt(inp: data); //encrypt using GCM
    return encrypted;
  }

  String decryptData(String data, String password) {
    var aesEncrypter = AesCrypt(key: password, padding: PaddingAES.pkcs7);
    String result = aesEncrypter.ecb.decrypt(enc: data);
    return result;
  }

  String safeString(String s) {
    return s.replaceAll(Constants.SPACE, Constants.UNDERSCORE).toLowerCase();
  }

  DoubleString createPassword(int len, String publicKey) {
    String fortunaKey = getRandomString(len);
    RsaKeyHelper tempHelper = RsaKeyHelper();
    String encryptedPassword = tempHelper.encrypt(
        fortunaKey, tempHelper.parsePublicKeyFromPem(publicKey));

    return DoubleString(fortunaKey, encryptedPassword);
  }

  bool unzip(String fileName) {
    var syncPath = convertPath(fileName);

    var exists = io.File(syncPath).existsSync();

    if (exists) {
      // Read the Zip file from disk.
      final bytes = io.File(syncPath).readAsBytesSync();

      // Decode the Zip file
      final archive = ZipDecoder().decodeBytes(bytes);

      // Extract the contents of the Zip archive to disk.
      for (final file in archive) {
        final filename = file.name;
        if (file.isFile) {
          final data = file.content as List<int>;
          io.File(filename)
            ..createSync(recursive: true)
            ..writeAsBytesSync(data);
        } else {
          io.Directory(filename)..create(recursive: true);
        }
      }

      return true;
    }

    return false;
  }
}

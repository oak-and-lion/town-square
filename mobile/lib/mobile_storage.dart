import 'dart:io';

import 'package:ext_storage/ext_storage.dart';
import 'package:mobile/istorage.dart';
import 'package:path_provider/path_provider.dart';

class MobileStorage implements IStorage {
  Future<String> getPathToDownload() async {
    if (Platform.isAndroid) {
      return await ExtStorage.getExternalStoragePublicDirectory(
          ExtStorage.DIRECTORY_DOWNLOADS); // /storage/emulated/0/Download
    } else if (Platform.isIOS) {
      return (await getApplicationDocumentsDirectory()).path;
    }
    return (await getApplicationDocumentsDirectory()).path;
  }
}

import '../mobile/lib/istorage.dart';

class ConsoleStorage implements IStorage {
  Future<String> getPathToDownload() async {
    return "./";
  }
}

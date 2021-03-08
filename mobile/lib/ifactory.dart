import 'isquare.dart';

import 'iapp.dart';
import 'iclient.dart';

class IFactory {
  IClient createClient(int type, String ip, int port) {
    return IClient();
  }

  ISquare createSquare(int type, String squareInfo, IApp app) {
    return ISquare();
  }
}

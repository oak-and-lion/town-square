import 'iget_clone.dart';
import 'iprocess_invitation.dart';
import 'isquare.dart';
import 'iapp.dart';
import 'iclient.dart';
import 'iutility.dart';

class IFactory {
  IClient createClient(int type, String ip, int port) {
    return IClient();
  }

  ISquare createSquare(int type, String squareInfo, IApp app) {
    return ISquare();
  }

  IProcessInvitation createProcessInvitation(
      int type, IUtility utility, IApp app) {
    return IProcessInvitation();
  }

  IGetClone createGetCone(int type, IUtility utility, IApp app) {
    return IGetClone();
  }
}

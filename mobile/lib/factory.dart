import 'get_clone.dart';
import 'iget_clone.dart';
import 'iprocess_invitation.dart';
import 'iutility.dart';
import 'process_invitation.dart';
import 'square.dart';
import 'ifactory.dart';
import 'client.dart';
import 'constants.dart';
import 'iapp.dart';
import 'iclient.dart';
import 'isquare.dart';

class TownSquareFactory implements IFactory {
  IClient createClient(int type, String ip, int port) {
    if (type == Constants.BASE_CLIENT_TYPE) {
      return Client(ip, port);
    }

    return IClient();
  }

  ISquare createSquare(int type, String squareInfo, IApp app) {
    if (type == Constants.BASE_SQUARE_TYPE) {
      List<String> info = squareInfo.split(Constants.COMMA);
      return Square(info[0], info[1], info[2], info[3], info[4], app);
    }

    return ISquare();
  }

  IProcessInvitation createProcessInvitation(
      int type, IUtility utility, IApp app) {
    if (type == Constants.BASE_PROCESS_INVITATION) {
      return ProcessInvitation(this, utility, app);
    }

    return IProcessInvitation();
  }

  IGetClone createGetCone(int type, IUtility utility, IApp app) {
    if (type == Constants.BASE_PROCESS_INVITATION) {
      return GetClone(this, utility, app);
    }

    return IGetClone();
  }
}

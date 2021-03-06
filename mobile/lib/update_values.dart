import 'constants.dart';

class UpdateValues {
  String _uniqueId;
  String _name;

  UpdateValues() {
    _uniqueId = Constants.EMPTY_STRING;
    _name = Constants.DEFAULT_NAME;
  }

  void setUniqueId(String value) {
    _uniqueId = value;
  }

  String getUniqueId() {
    return _uniqueId;
  }

  void setName(String value) {
    _name = value;
  }

  String getName() {
    return _name;
  }
}

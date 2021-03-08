import 'constants.dart';

class SquareResponse {
  String _code;
  String _data;
  String _raw;

  SquareResponse(this._code, this._data, this._raw);

  String getCode() {
    return _code;
  }

  String getData() {
    return _data;
  }

  String getRaw() {
    return _raw;
  }

  List<String> getSplit() {
    return _raw.split(Constants.COLON);
  }
}

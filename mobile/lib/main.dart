import 'package:flutter/material.dart';
import 'package:mobile/utililty.dart';
import 'constants.dart';
import 'iutility.dart';
import 'utililty.dart';

void main() {
  IUtility utility = new Utility();
  runApp(MyApp(utility));
}

class MyApp extends StatelessWidget {
  final IUtility _utility;

  MyApp(this._utility) {
    _utility.init().whenComplete(() => initialize());
  }

  void initialize() {
    if (!_utility.checkFileExists(Constants.uniqueIdFile)) {
      _utility.writeFile(Constants.uniqueIdFile, _utility.createGUID());
    }
  }

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        title: Constants.title,
        theme: ThemeData(
          primarySwatch: Colors.blue,
        ),
        home: Scaffold(
            appBar: AppBar(title: Text(Constants.title)),
            body: Center(
                child: Text(_utility.readFile(Constants.uniqueIdFile)))));
  }
}

import 'package:flutter/material.dart';
import 'package:mobile/utililty.dart';
import 'constants.dart';
import 'iutility.dart';
import 'utililty.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  MyApp({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'Flutter Demo',
      home: new MyTabbedPage(),
    );
  }
}

class MyTabbedPage extends StatefulWidget {
  const MyTabbedPage({Key key}) : super(key: key);

  @override
  _MyAppState createState() => new _MyAppState(new Utility());
}

class _MyAppState extends State<MyTabbedPage> {
  final IUtility _utility;
  String _titleText;
  bool _isLoading;

  _MyAppState(this._utility) {
    _titleText = "waiting";
    _isLoading = true;
  }

  void initialize() {
    if (!_utility.checkFileExists(Constants.uniqueIdFile)) {
      _utility.writeFile(Constants.uniqueIdFile, _utility.createGUID());
    }

    setValues(_utility.readFile(Constants.uniqueIdFile));
    _isLoading = false;
  }

  Future<void> initFutures() async {
    List<Future<void>> futures = [];

    futures[0] = _utility.init(initialize);

    await Future.wait(futures);
  }

  void setValues(String titleText) {
    setState(() {
      _titleText = titleText;
    });
  }

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return FutureBuilder(
          future: initFutures(),
          builder: (BuildContext context, AsyncSnapshot<void> m) {
            return buildMain();
          });
    } else {
      return buildMain();
    }
  }

  Widget buildMain() {
    return MaterialApp(
        title: Constants.title,
        theme: ThemeData(
          primarySwatch: Colors.blue,
        ),
        home: Scaffold(
            appBar: AppBar(title: Text(Constants.title)),
            body: Center(child: Text(_titleText))));
  }
}

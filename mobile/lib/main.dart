import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:mobile/about.dart';
import 'package:mobile/iapp.dart';
import 'package:mobile/mobile_storage.dart';
import 'package:mobile/settings_tab.dart';
import 'package:mobile/update_values.dart';
import 'package:mobile/utililty.dart';
import 'package:mobile/waiting_screen.dart';
import 'constants.dart';
import 'iutility.dart';
import 'iview.dart';
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
  _MyAppState createState() =>
      new _MyAppState(new Utility(new MobileStorage(), Constants.EMPTY_STRING));
}

class _MyAppState extends State<MyTabbedPage>
    with SingleTickerProviderStateMixin
    implements IView, IApp {
  final IUtility _utility;
  bool _isLoading;
  UpdateValues _updateValues;
  List<Widget> _tabChildren;
  int _numberOfTabs;
  int _initialTabIndex;
  TabController _tabController;
  List<Tab> _tabs;
  SettingsTab _settingsTab;

  _MyAppState(this._utility) {
    _isLoading = true;
    _updateValues = new UpdateValues();
    _initialTabIndex = 0;
    _numberOfTabs = 1;
    _tabController = null;
    _tabChildren = [];
    _tabs = [];
    _tabs.add(Tab(icon: Icon(Icons.info)));
    _tabChildren.add(buildAbout());
    _settingsTab = new SettingsTab(new TextEditingController(),
        new TextEditingController(), new FocusNode(), this);
  }

  void sendMessage(String msg) {}

  void noSquares() {}

  void processInvitation(String invitation) {}

  void initialize() {
    setState(() {
      _tabChildren.add(buildSettingsTab());
      _tabs.add(Tab(icon: Icon(Icons.settings)));
      _numberOfTabs = _tabChildren.length;
      _isLoading = false;
    });

    setValues();
  }

  Future<void> initFutures() async {
    List<Future<void>> futures = [];

    if (_isLoading) {
      futures[0] = _utility.init(initialize);
    }

    await Future.wait(futures);
  }

  void setValues() {
    setState(() {
      _settingsTab.setNameValue(_updateValues.getName());
      _settingsTab.setUniqueIdValue(_updateValues.getUniqueId());
    });
  }

  void setName(String value) {
    _updateValues.setName(value);
    setValues();
  }

  void start() {}

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
            return new Waiting();
          });
    } else {
      return buildMain();
    }
  }

  Widget buildMain() {
    if (_tabController == null) {
      _tabController = new TabController(
          length: _numberOfTabs, vsync: this, initialIndex: _initialTabIndex);
    }
    return MaterialApp(
        title: Constants.TITLE,
        theme: ThemeData(
          primarySwatch: Colors.blue,
        ),
        home: Scaffold(
            appBar: AppBar(
              title: Text(Constants.TITLE),
              bottom: TabBar(controller: _tabController, tabs: _tabs),
            ),
            body: TabBarView(
              children: _tabChildren,
              controller: _tabController,
            )));
  }

  Widget buildSettingsTab() {
    return _settingsTab;
  }

  Widget buildAbout() {
    return new About();
  }
}

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'about.dart';
import 'app.dart';
import 'factory.dart';
import 'iapp.dart';
import 'ifactory.dart';
import 'iprocess_invitation.dart';
import 'isquare.dart';
import 'mobile_storage.dart';
import 'rsa_pem.dart';
import 'settings_tab.dart';
import 'update_values.dart';
import 'utililty.dart';
import 'waiting_screen.dart';
import 'constants.dart';
import 'iutility.dart';
import 'iview.dart';

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
  _MyAppState createState() {
    IUtility utility = new Utility(new MobileStorage(), Constants.EMPTY_STRING);
    IView view = IView();
    IFactory fact = new TownSquareFactory();
    List<ISquare> squares = [];
    RsaKeyHelper helper = RsaKeyHelper();
    IApp app = new App(utility, view, helper, fact, squares);
    return new _MyAppState(utility, new TownSquareFactory(), app);
  }
}

class _MyAppState extends State<MyTabbedPage>
    with SingleTickerProviderStateMixin
    implements IView {
  final IUtility _utility;
  bool _isLoading;
  UpdateValues _updateValues;
  List<Widget> _tabChildren;
  int _numberOfTabs;
  int _initialTabIndex;
  TabController _tabController;
  List<Tab> _tabs;
  SettingsTab _settingsTab;
  IFactory _factory;
  IApp _app;

  _MyAppState(this._utility, this._factory, this._app) {
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
        new TextEditingController(), new FocusNode(), _app);
  }

  void sendMessage(String msg) {}

  void noSquares() {}

  void processInvitation(String invitation) {
    IProcessInvitiation processInvitation = _factory.createProcessInvitation(
        Constants.BASE_PROCESS_INVITATION, _utility, _app);

    processInvitation.processInvitation(invitation);
  }

  void registerHub(String hubInfo, String hubName) {
    _utility.writeFile(hubName + Constants.HUB_REGISTRATION_FILE_EXT, hubInfo);
  }

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

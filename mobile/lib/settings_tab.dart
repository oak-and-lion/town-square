import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:mobile/constants.dart';

import 'iapp.dart';

class SettingsTab extends StatelessWidget {
  final TextEditingController _uniqueId;
  final TextEditingController _nameController;
  final FocusNode _focusNode;
  final IApp _app;

  SettingsTab(this._uniqueId, this._nameController, this._focusNode, this._app);

  void setNameValue(String value) {
    _nameController.text = value;
  }

  void setUniqueIdValue(String value) {
    _uniqueId.text = value;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: Colors.white54,
        body: SingleChildScrollView(
            child: Padding(
                padding: EdgeInsets.fromLTRB(20, 20, 20, 10),
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: <Widget>[
                      Text(Constants.UNIQUE_ID_LABEL),
                      Text(_uniqueId.text),
                      Text(Constants.EMPTY_STRING),
                      Text(Constants.NAME_LABEL),
                      Padding(
                          padding: EdgeInsets.all(Constants.NAME_PADDING),
                          child: TextField(
                              textAlign: TextAlign.center,
                              maxLength: Constants.NAME_LENGTH,
                              focusNode: _focusNode,
                              controller: _nameController,
                              decoration: InputDecoration(
                                  filled: true,
                                  fillColor: Colors.white60,
                                  border: new OutlineInputBorder(
                                      borderRadius: const BorderRadius.all(
                                        const Radius.circular(
                                            Constants.TEXTFIELD_BORDER_RADIUS),
                                      ),
                                      borderSide: BorderSide(
                                          color: Colors.blue,
                                          width: Constants
                                              .TEXTFIELD_BORDER_WIDTH)),
                                  enabledBorder: OutlineInputBorder(
                                    borderRadius: const BorderRadius.all(
                                      const Radius.circular(
                                          Constants.TEXTFIELD_BORDER_RADIUS),
                                    ),
                                    borderSide: BorderSide(
                                        color: Colors.blue,
                                        width:
                                            Constants.TEXTFIELD_BORDER_WIDTH),
                                  ),
                                  focusedBorder: OutlineInputBorder(
                                    borderRadius: const BorderRadius.all(
                                      const Radius.circular(
                                          Constants.TEXTFIELD_BORDER_RADIUS),
                                    ),
                                    borderSide: BorderSide(
                                        color: Colors.blue,
                                        width:
                                            Constants.TEXTFIELD_BORDER_WIDTH),
                                  ),
                                  hintText: Constants.EMAIL_HINT_TEXT))),
                      ElevatedButton(
                          onPressed: () => {_app.setName(_nameController.text)},
                          child: Text(
                            Constants.SAVE_SETTINGS_BUTTON_TEXT,
                            style: TextStyle(
                                fontSize: Constants.INFORMED_MESSAGE_FONT_SIZE),
                          ))
                    ]))));
  }
}

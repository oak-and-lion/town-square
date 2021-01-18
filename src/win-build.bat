@echo off
echo cleaning staging directory
del /F /Q staging\*.*
rmdir /S /Q staging
mkdir staging

echo building javac class files
javac --module-path "C:/Users/wewan/javafx-sdk-11.0.2/lib" --add-modules "javafx.controls,javafx.fxml" -d staging AlertBox.java App.java Client.java ClientThread.java Constants.java CryptoUtils.java DialogController.java EncryptorAesGcmPassword.java Factory.java FileWriteResponse.java IAlert.java IAlertBox.java IApp.java IClient.java IClientThread.java ICryptoUtils.java IDialogController.java ILogIt.java IPAddress.java IServer.java IServerThread.java ISquare.java ISquareController.java ISquareKeyPair.java ISystemExit.java ITextDialogBox.java ITextDialogBoxCallback.java IUtility.java LogItConsole.java LogItFile.java Server.java ServerThread.java Square.java SquareController.java SquareKeyPair.java SquarePost.java SquareResponse.java SystemExit.java TextDialogBox.java TownSquareAlert.java TownSquareButton.java Utility.java

echo copying sample.xml
copy sample.fxml staging\sample.fxml
echo copying manifest file
copy manifest.txt staging\manifest.txt

cd staging

mkdir output

cd output

copy ..\..\..\javafx-windows.zip
copy ..\..\..\jdk-11.0.9_windows-x64_bin.zip
copy ..\..\..\town-square-windows.bat

jar -c -v -m ..\manifest.txt -f App.jar ..\*.class ..\sample.fxml

mkdir package

tar -a -c -f package\pkg-town-square_windows.zip town-square-windows.bat App.jar javafx-windows.zip jdk-11.0.9_windows-x64_bin.zip

cd package
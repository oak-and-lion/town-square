@echo off
echo cleaning staging directory
del /F /Q staging\*.*
rmdir /S /Q staging
mkdir staging

echo building javac class files
rem edit the module path below to point to your installation of the java fx sdk
javac --module-path "C:/Users/wewan/javafx-sdk-11.0.2/lib" --add-modules "javafx.controls,javafx.fxml" -d staging *.java

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
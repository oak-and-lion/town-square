@echo off
echo cleaning staging directory
del /F /Q staging\*.*
rmdir /S /Q staging
mkdir staging

echo building javac class files
SET JAVAFX_PATH="C:/Users/wewan/javafx-sdk-11.0.2/lib"
javac --module-path %JAVAFX_PATH% --add-modules "javafx.controls,javafx.fxml,javafx.media" -d staging *.java

del staging\xx*
del staging\test.class
del staging\clientcmdtest.class

echo copying sample.xml
copy sample.fxml staging\sample.fxml
echo copying manifest file
copy manifest.txt staging\manifest.txt

cd staging

mkdir output

cd output

echo copying runtime dependencies
copy ..\..\..\javafx-windows.zip
copy ..\..\..\jdk-11.0.9_windows-x64_bin.zip
copy ..\..\..\town-square-windows.bat
copy ..\..\..\blocked-image.png
copy ..\..\..\blocked-video.mp4

echo creating JAR file

jar -c -v -m ..\manifest.txt -f App.jar ..\*.class ..\sample.fxml

echo creating final package

mkdir package

tar -a -c -f package\pkg-town-square_windows.zip town-square-windows.bat App.jar javafx-windows.zip jdk-11.0.9_windows-x64_bin.zip blocked-image.png blocked-video.mp4

cd package

dir
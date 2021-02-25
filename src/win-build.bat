@echo off
echo cleaning staging directory
del /F /Q staging\*.*
rmdir /S /Q staging
mkdir staging

echo building javac class files
SET JAVAFX_PATH="C:/java/javafx/lib"
javac --module-path %JAVAFX_PATH% --add-modules "javafx.controls,javafx.fxml,javafx.media" -d staging *.java

del staging\xx*
del staging\test.class
del staging\clientcmdtest.class

echo copying sample.xml
copy sample.fxml staging\sample.fxml
echo copying manifest file
copy Manifest.txt staging\Manifest.txt
copy ManifestHub.txt staging\ManifestHub.txt

set /a increment=1
set /p Major=<ver-major.txt
set /p Minor=<ver-minor.txt
set /p Build=<ver-build.txt
set /a Build=%Build%+%increment%
if %Build% gtr 99 (
    set /a Build=0
    set /a Minor=%Minor%+%increment%
    if %Minor% gtr 99 (
        set /a Minor=0
        set /a Major=%Major%+%increment%
    )
)

echo %Build%>ver-build.txt
echo %Minor%>ver-minor.txt
echo %Major%>ver-major.txt
echo public class ConstantVersion {private ConstantVersion(){}public static final String VERSION = "%Major: =%.%Minor: =%.%Build: =%";}>ConstantVersion.java
echo version %Major%.%Minor%.%Build%

cd staging

mkdir output

cd output

echo copying runtime dependencies
copy ..\..\..\javafx-windows.zip
copy ..\..\..\jdk-11.0.9_windows-x64_bin.zip
copy ..\..\..\town-square-windows.bat
copy ..\..\..\town-square-windows-hub.bat
copy ..\..\..\blocked-image.png
copy ..\..\..\blocked-video.mp4

echo creating JAR file

jar -c -v -m ..\Manifest.txt -f App.jar ..\*.class ..\sample.fxml
jar -c -v -m ../ManifestHub.txt -f Hub.jar ../*.class

echo creating final package

mkdir package

tar -a -c -f package\pkg-town-square_windows.zip town-square-windows.bat town-square-windows-hub.bat App.jar Hub.jar javafx-windows.zip jdk-11.0.9_windows-x64_bin.zip blocked-image.png blocked-video.mp4

cd package

dir
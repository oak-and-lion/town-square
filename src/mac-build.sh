echo cleaning staging directory
rm -f -R staging/
mkdir staging

echo building javac class files
JAVA_PATH="jdk-11.0.2.jdk/Contents/Home/bin"
JAVAFX_PATH="javafx/lib"
$JAVA_PATH/javac --module-path $JAVAFX_PATH --add-modules "javafx.controls,javafx.fxml,javafx.media" -d staging *.java

rm staging/xx*
rm staging/Test.class
rm staging/ClientCmdTest.class


echo copying sample.xml
cp sample.fxml staging/sample.fxml
echo copying manifest files
cp Manifest.txt staging/Manifest.txt
cp ManifestHub.txt staging/ManifestHub.txt

cd staging

mkdir output

cd output

echo copying runtime dependencies

cp ../../../javafx-mac.zip javafx-mac.zip
cp ../../../openjdk-11.0.2_osx-x64_bin.tar.gz openjdk-11.0.2_osx-x64_bin.tar.gz
cp ../../../town-square-mac.sh town-square-mac.sh
cp ../../../town-square-mac-hub.sh town-square-mac-hub.sh
cp ../../../blocked-image.png blocked-image.png
cp ../../../blocked-video.mp4 blocked-video.mp4

echo creating JAR file

jar -c -v -m ../Manifest.txt -f App.jar ../*.class ../sample.fxml
jar -c -v -m ../ManifestHub.txt -f Hub.jar ../*.class

echo creating final package

mkdir package

tar -a -c -f package/pkg-town-square_mac.tar.gz town-square-mac.sh town-square-mac-hub.sh App.jar Hub.jar javafx-mac.zip openjdk-11.0.2_osx-x64_bin.tar.gz blocked-image.png blocked-video.mp4

cd package

ls


echo cleaning staging directory
rm -f -R staging/
mkdir staging

echo building javac class files
JAVA_PATH="../jdk-11.0.10+9/bin"
JAVAFX_PATH="../javafx/lib"
#PATH='$JAVA_PATH:$PATH'
$JAVA_PATH/javac --module-path $JAVAFX_PATH --add-modules "javafx.controls,javafx.fxml,javafx.media" -d staging *.java

rm staging/xx*
rm staging/Test.class
rm staging/ClientCmdTest.class

echo copying sample.xml
cp sample.fxml staging/sample.fxml
echo copying manifest files
cp Manifest.txt staging/Manifest.txt

cd staging

mkdir output

cd output

cp ../../../javafx-linux.zip javafx-linux.zip
cp ../../../OpenJDK11U-jdk_x64_linux_hotspot_11.0.10_9.tar.gz OpenJDK11U-jdk_x64_linux_hotspot_11.0.10_9.tar.gz
cp ../../../town-square-linux.sh town-square-linux.sh

../../$JAVA_PATH/jar -c -v -m ../Manifest.txt -f App.jar ../*.class ../sample.fxml

mkdir package

tar -a -c -f package/pkg-town-square_linux.zip town-square-linux.sh App.jar javafx-linux.zip OpenJDK11U-jdk_x64_linux_hotspot_11.0.10_9.tar.gz

cd package

ls
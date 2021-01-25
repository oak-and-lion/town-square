echo cleaning staging directory
rm -f -R staging/
mkdir staging

echo building javac class files
JAVA_PATH="jdk-11.0.2.jdk/Contents/Home/bin"
JAVAFX_PATH="javafx/lib"
$JAVA_PATH/javac --module-path $JAVAFX_PATH --add-modules "javafx.controls,javafx.fxml" -d staging *.java

rm staging/xx*
rm staging/Test.class
rm staging/ClientCmdTest.class


echo copying sample.xml
cp sample.fxml staging/sample.fxml
echo copying manifest files
cp manifest.txt staging/manifest.txt

cd staging

mkdir output

cd output

cp ../../../javafx-mac.zip javafx-mac.zip
cp ../../../openjdk-11.0.2_osx-x64_bin.tar.gz openjdk-11.0.2_osx-x64_bin.tar.gz
cp ../../../town-square-mac.sh town-square-mac.sh

jar -c -v -m ../manifest.txt -f App.jar ../*.class ../sample.fxml

mkdir package

tar -a -c -f package/pkg-town-square_mac.zip town-square-mac.sh App.jar javafx-mac.zip openjdk-11.0.2_osx-x64_bin.tar.gz

cd package

ls


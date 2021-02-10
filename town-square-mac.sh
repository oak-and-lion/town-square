echo verifying Java FX dependency

[ ! -d "javafx" ] && 
    tar xopf javafx-mac.zip

echo verifying Java runtime dependency

[ ! -d "jdk-11.0.2.jdk" ] && 
    gunzip -c openjdk-11.0.2_osx-x64_bin.tar.gz| tar xopf -

echo starting Town Square

./jdk-11.0.2.jdk/Contents/Home/bin/java --module-path "javafx/lib" --add-modules "javafx.controls,javafx.fxml,javafx.media" -jar App.jar